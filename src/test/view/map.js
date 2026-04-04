(function () {
  const state = {
    building: null,
    floor: null,
    floorIndex: -1,
    selectedKey: null,
    selectedElement: null,
    transform: {
      baseScale: 1,
      baseX: 0,
      baseY: 0,
      zoom: 1,
      panX: 0,
      panY: 0
    },
    drag: {
      active: false,
      startX: 0,
      startY: 0,
      panX: 0,
      panY: 0
    }
  };

  const dom = {};

  function init() {
    cacheDom();
    window.DebugApi.setupGlobalControls();
    bindActions();
    renderFloorSelect();
    setViewerStatus("Load a building or floor to start.");
    setError("No errors.");
  }

  function cacheDom() {
    dom.buildingId = document.getElementById("buildingId");
    dom.floorId = document.getElementById("floorId");
    dom.floorSelect = document.getElementById("floorSelect");
    dom.viewerStatus = document.getElementById("viewerStatus");
    dom.errorBox = document.getElementById("errorBox");
    dom.selectedInfo = document.getElementById("selectedInfo");
    dom.objectList = document.getElementById("objectList");
    dom.mapViewport = document.getElementById("mapViewport");
    dom.mapSvg = document.getElementById("mapSvg");
    dom.world = document.getElementById("world");

    dom.layers = {
      floor: document.getElementById("layer-floor"),
      rooms: document.getElementById("layer-rooms"),
      stairs: document.getElementById("layer-stairs"),
      edges: document.getElementById("layer-edges"),
      nodes: document.getElementById("layer-nodes"),
      labels: document.getElementById("layer-labels")
    };

    dom.showRooms = document.getElementById("showRooms");
    dom.showStairs = document.getElementById("showStairs");
    dom.showNodes = document.getElementById("showNodes");
    dom.showEdges = document.getElementById("showEdges");
    dom.showLabels = document.getElementById("showLabels");
  }

  function bindActions() {
    document.getElementById("loadBuildingBtn").addEventListener("click", loadBuilding);
    document.getElementById("loadFloorBtn").addEventListener("click", loadFloorDirect);
    document.getElementById("renderSelectedFloorBtn").addEventListener("click", renderSelectedFloorFromDropdown);
    document.getElementById("fitViewBtn").addEventListener("click", fitToCurrentFloor);
    document.getElementById("resetViewBtn").addEventListener("click", resetPanZoom);

    [dom.showRooms, dom.showStairs, dom.showNodes, dom.showEdges, dom.showLabels].forEach((checkbox) => {
      checkbox.addEventListener("change", () => {
        applyLayerVisibility();
        renderObjectList();
      });
    });

    dom.floorSelect.addEventListener("change", () => {
      const value = Number(dom.floorSelect.value);
      if (!Number.isNaN(value)) {
        state.floorIndex = value;
      }
    });

    dom.mapViewport.addEventListener("wheel", onWheelZoom, { passive: false });
    dom.mapViewport.addEventListener("mousedown", onDragStart);
    window.addEventListener("mousemove", onDragMove);
    window.addEventListener("mouseup", onDragEnd);
  }

  async function loadBuilding() {
    const id = Number(dom.buildingId.value);
    if (!id) {
      setError("Enter a valid Building ID.");
      return;
    }

    setError("No errors.");
    setViewerStatus(`Loading building ${id} ...`);

    try {
      const response = await window.DebugApi.fetchJson({
        path: `/map/buildings/${id}`
      });
      state.building = response.payload;
      state.floorIndex = 0;
      renderFloorSelect();
      if (Array.isArray(state.building.floors) && state.building.floors.length > 0) {
        state.floor = state.building.floors[0];
        renderCurrentFloor(true);
        setViewerStatus(`Building loaded: ${state.building.building.name || id}`);
      } else {
        state.floor = null;
        clearLayers();
        renderObjectList();
        dom.selectedInfo.textContent = "Nothing selected.";
        setViewerStatus("Building loaded, but no floors found.");
      }
    } catch (error) {
      handleError(error);
    }
  }

  async function loadFloorDirect() {
    const id = Number(dom.floorId.value);
    if (!id) {
      setError("Enter a valid Floor ID.");
      return;
    }

    setError("No errors.");
    setViewerStatus(`Loading floor ${id} ...`);

    try {
      const response = await window.DebugApi.fetchJson({
        path: `/map/floors/${id}`
      });
      state.floor = response.payload;
      state.floorIndex = -1;
      renderFloorSelect();
      renderCurrentFloor(true);
      const floorName = state.floor.floor && state.floor.floor.name ? state.floor.floor.name : id;
      setViewerStatus(`Floor loaded: ${floorName}`);
    } catch (error) {
      handleError(error);
    }
  }

  function renderSelectedFloorFromDropdown() {
    if (!state.building || !Array.isArray(state.building.floors) || state.building.floors.length === 0) {
      setError("Load a building with floors first.");
      return;
    }

    const index = Number(dom.floorSelect.value);
    if (Number.isNaN(index) || index < 0 || index >= state.building.floors.length) {
      setError("Selected floor index is invalid.");
      return;
    }

    state.floorIndex = index;
    state.floor = state.building.floors[index];
    renderCurrentFloor(true);
    const floorInfo = state.floor.floor || {};
    setViewerStatus(`Rendered floor ${floorInfo.number || "?"}: ${floorInfo.name || "Unnamed"}`);
    setError("No errors.");
  }

  function renderFloorSelect() {
    const options = [];
    if (state.building && Array.isArray(state.building.floors) && state.building.floors.length > 0) {
      state.building.floors.forEach((item, index) => {
        const info = item.floor || {};
        options.push(`<option value="${index}">#${info.number || "?"} ${escapeHtml(info.name || "Unnamed")}</option>`);
      });
      dom.floorSelect.innerHTML = options.join("");
      if (state.floorIndex >= 0 && state.floorIndex < state.building.floors.length) {
        dom.floorSelect.value = String(state.floorIndex);
      }
    } else {
      dom.floorSelect.innerHTML = "<option value='-1'>No floors loaded</option>";
      dom.floorSelect.value = "-1";
    }
  }

  function renderCurrentFloor(resetTransform) {
    clearLayers();
    clearSelection();

    if (!state.floor) {
      renderObjectList();
      dom.selectedInfo.textContent = "Nothing selected.";
      return;
    }

    drawFloorPolygon();
    drawRooms();
    drawStairs();
    drawNodeEdges();
    drawNodes();
    drawLabels();

    if (resetTransform) {
      fitToCurrentFloor();
    }

    applyLayerVisibility();
    renderObjectList();
  }

  function drawFloorPolygon() {
    const floorInfo = state.floor.floor || {};
    const points = normalizePoints(floorInfo.points);
    if (points.length < 3) {
      return;
    }

    const polygon = makePolygon(points, "shape floor-shape", {
      key: "floor",
      type: "floor",
      payload: floorInfo
    });
    dom.layers.floor.appendChild(polygon);
  }

  function drawRooms() {
    const rooms = Array.isArray(state.floor.rooms) ? state.floor.rooms : [];
    rooms.forEach((room, index) => {
      const points = normalizePoints(room.points);
      if (points.length < 3) {
        return;
      }
      const polygon = makePolygon(points, "shape room-shape", {
        key: `room-${room.id || index}`,
        type: "room",
        payload: room
      });
      dom.layers.rooms.appendChild(polygon);
    });
  }

  function drawStairs() {
    const stairs = Array.isArray(state.floor.stairs) ? state.floor.stairs : [];
    stairs.forEach((item, index) => {
      const points = normalizePoints(item.points);
      if (points.length < 3) {
        return;
      }
      const polygon = makePolygon(points, "shape stairs-shape", {
        key: `stairs-${item.id || index}`,
        type: "stairs",
        payload: item
      });
      dom.layers.stairs.appendChild(polygon);
    });
  }

  function drawNodes() {
    const nodes = Array.isArray(state.floor.nodes) ? state.floor.nodes : [];
    nodes.forEach((node, index) => {
      const pos = normalizePos(node.pos);
      if (!pos) {
        return;
      }
      const circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
      circle.setAttribute("cx", pos.x);
      circle.setAttribute("cy", pos.y);
      circle.setAttribute("r", 5);
      circle.setAttribute("class", "shape node-shape");
      bindShapeSelection(circle, {
        key: `node-${node.id || index}`,
        type: "node",
        payload: node
      });
      dom.layers.nodes.appendChild(circle);
    });
  }

  function drawNodeEdges() {
    const nodes = Array.isArray(state.floor.nodes) ? state.floor.nodes : [];
    const byId = {};
    nodes.forEach((node) => {
      if (node && node.id != null) {
        byId[node.id] = node;
      }
    });

    nodes.forEach((node) => {
      if (!node || !Array.isArray(node.neighbors)) {
        return;
      }
      const from = normalizePos(node.pos);
      if (!from) {
        return;
      }

      node.neighbors.forEach((neighborId) => {
        if (node.id != null && neighborId < node.id) {
          return;
        }
        const neighbor = byId[neighborId];
        if (!neighbor) {
          return;
        }
        const to = normalizePos(neighbor.pos);
        if (!to) {
          return;
        }
        const line = document.createElementNS("http://www.w3.org/2000/svg", "line");
        line.setAttribute("x1", from.x);
        line.setAttribute("y1", from.y);
        line.setAttribute("x2", to.x);
        line.setAttribute("y2", to.y);
        line.setAttribute("class", "node-edge");
        dom.layers.edges.appendChild(line);
      });
    });
  }

  function drawLabels() {
    const rooms = Array.isArray(state.floor.rooms) ? state.floor.rooms : [];
    rooms.forEach((room) => {
      const center = centroid(normalizePoints(room.points));
      if (!center) {
        return;
      }
      const text = document.createElementNS("http://www.w3.org/2000/svg", "text");
      text.setAttribute("x", center.x);
      text.setAttribute("y", center.y);
      text.setAttribute("class", "map-label");
      text.textContent = room.name || "room";
      dom.layers.labels.appendChild(text);
    });
  }

  function makePolygon(points, className, meta) {
    const polygon = document.createElementNS("http://www.w3.org/2000/svg", "polygon");
    polygon.setAttribute("points", pointsToSvg(points));
    polygon.setAttribute("class", className);
    bindShapeSelection(polygon, meta);
    return polygon;
  }

  function bindShapeSelection(element, meta) {
    element.dataset.key = meta.key;
    element.addEventListener("click", (event) => {
      event.stopPropagation();
      selectObject(meta, element);
    });
  }

  function selectObject(meta, element) {
    state.selectedKey = meta.key;
    if (state.selectedElement) {
      state.selectedElement.classList.remove("active-shape");
    }
    state.selectedElement = element;
    state.selectedElement.classList.add("active-shape");

    dom.selectedInfo.textContent = window.DebugApi.formatJson({
      type: meta.type,
      payload: meta.payload
    });

    const rows = dom.objectList.querySelectorAll("button");
    rows.forEach((row) => {
      row.classList.toggle("is-active", row.dataset.key === meta.key);
    });
  }

  function clearSelection() {
    state.selectedKey = null;
    if (state.selectedElement) {
      state.selectedElement.classList.remove("active-shape");
      state.selectedElement = null;
    }
  }

  function renderObjectList() {
    if (!state.floor) {
      dom.objectList.innerHTML = "<div class='small'>No floor data.</div>";
      return;
    }

    const rows = [];
    const floorInfo = state.floor.floor || {};
    rows.push(makeObjectButton("floor", `Floor #${floorInfo.number || "?"} ${floorInfo.name || ""}`));

    if (dom.showRooms.checked) {
      const rooms = Array.isArray(state.floor.rooms) ? state.floor.rooms : [];
      rooms.forEach((room, index) => rows.push(makeObjectButton(`room-${room.id || index}`, `Room: ${room.name || room.id || index}`)));
    }

    if (dom.showStairs.checked) {
      const stairs = Array.isArray(state.floor.stairs) ? state.floor.stairs : [];
      stairs.forEach((item, index) => rows.push(makeObjectButton(`stairs-${item.id || index}`, `Stairs: ${item.id || index}`)));
    }

    if (dom.showNodes.checked) {
      const nodes = Array.isArray(state.floor.nodes) ? state.floor.nodes : [];
      nodes.forEach((node, index) => rows.push(makeObjectButton(`node-${node.id || index}`, `Node: ${node.id || index}`)));
    }

    dom.objectList.innerHTML = rows.join("");
    dom.objectList.querySelectorAll("button").forEach((button) => {
      button.addEventListener("click", () => {
        const shape = dom.mapSvg.querySelector(`[data-key='${button.dataset.key}']`);
        if (shape) {
          shape.dispatchEvent(new MouseEvent("click", { bubbles: true }));
        }
      });
      button.classList.toggle("is-active", button.dataset.key === state.selectedKey);
    });
  }

  function makeObjectButton(key, label) {
    return `<button type='button' class='object-item' data-key='${escapeHtml(key)}'>${escapeHtml(label)}</button>`;
  }

  function applyLayerVisibility() {
    dom.layers.rooms.style.display = dom.showRooms.checked ? "" : "none";
    dom.layers.stairs.style.display = dom.showStairs.checked ? "" : "none";
    dom.layers.nodes.style.display = dom.showNodes.checked ? "" : "none";
    dom.layers.edges.style.display = dom.showEdges.checked ? "" : "none";
    dom.layers.labels.style.display = dom.showLabels.checked ? "" : "none";
  }

  function clearLayers() {
    Object.keys(dom.layers).forEach((key) => {
      dom.layers[key].innerHTML = "";
    });
  }

  function fitToCurrentFloor() {
    const points = collectAllPoints();
    if (points.length === 0) {
      resetPanZoom();
      return;
    }

    const bounds = getBounds(points);
    const width = Math.max(bounds.maxX - bounds.minX, 1);
    const height = Math.max(bounds.maxY - bounds.minY, 1);

    const viewportRect = dom.mapViewport.getBoundingClientRect();
    const padding = 24;
    const scaleX = (viewportRect.width - padding * 2) / width;
    const scaleY = (viewportRect.height - padding * 2) / height;

    state.transform.baseScale = Math.max(Math.min(scaleX, scaleY), 0.01);
    state.transform.baseX = padding - bounds.minX * state.transform.baseScale;
    state.transform.baseY = padding - bounds.minY * state.transform.baseScale;
    state.transform.zoom = 1;
    state.transform.panX = 0;
    state.transform.panY = 0;

    applyTransform();
  }

  function resetPanZoom() {
    state.transform.zoom = 1;
    state.transform.panX = 0;
    state.transform.panY = 0;
    applyTransform();
  }

  function applyTransform() {
    const t = state.transform;
    const scale = t.baseScale * t.zoom;
    const tx = t.baseX + t.panX;
    const ty = t.baseY + t.panY;
    dom.world.setAttribute("transform", `translate(${tx}, ${ty}) scale(${scale})`);
  }

  function onWheelZoom(event) {
    event.preventDefault();
    if (!state.floor) {
      return;
    }

    const direction = event.deltaY < 0 ? 1.12 : 0.88;
    state.transform.zoom = clamp(state.transform.zoom * direction, 0.25, 12);
    applyTransform();
  }

  function onDragStart(event) {
    state.drag.active = true;
    state.drag.startX = event.clientX;
    state.drag.startY = event.clientY;
    state.drag.panX = state.transform.panX;
    state.drag.panY = state.transform.panY;
    dom.mapViewport.classList.add("is-dragging");
  }

  function onDragMove(event) {
    if (!state.drag.active) {
      return;
    }
    state.transform.panX = state.drag.panX + (event.clientX - state.drag.startX);
    state.transform.panY = state.drag.panY + (event.clientY - state.drag.startY);
    applyTransform();
  }

  function onDragEnd() {
    state.drag.active = false;
    dom.mapViewport.classList.remove("is-dragging");
  }

  function collectAllPoints() {
    if (!state.floor) {
      return [];
    }

    const all = [];
    const floorInfo = state.floor.floor || {};
    all.push.apply(all, normalizePoints(floorInfo.points));

    const rooms = Array.isArray(state.floor.rooms) ? state.floor.rooms : [];
    rooms.forEach((room) => all.push.apply(all, normalizePoints(room.points)));

    const stairs = Array.isArray(state.floor.stairs) ? state.floor.stairs : [];
    stairs.forEach((item) => all.push.apply(all, normalizePoints(item.points)));

    const nodes = Array.isArray(state.floor.nodes) ? state.floor.nodes : [];
    nodes.forEach((node) => {
      const pos = normalizePos(node.pos);
      if (pos) {
        all.push(pos);
      }
    });

    return all;
  }

  function normalizePoints(points) {
    if (!Array.isArray(points)) {
      return [];
    }
    return points
      .map((point) => ({ x: Number(point.x), y: Number(point.y) }))
      .filter((point) => Number.isFinite(point.x) && Number.isFinite(point.y));
  }

  function normalizePos(pos) {
    if (!pos || typeof pos !== "object") {
      return null;
    }
    const x = Number(pos.x);
    const y = Number(pos.y);
    if (!Number.isFinite(x) || !Number.isFinite(y)) {
      return null;
    }
    return { x, y };
  }

  function pointsToSvg(points) {
    return points.map((point) => `${point.x},${point.y}`).join(" ");
  }

  function centroid(points) {
    if (!points || points.length === 0) {
      return null;
    }
    const sum = points.reduce((acc, p) => ({ x: acc.x + p.x, y: acc.y + p.y }), { x: 0, y: 0 });
    return { x: sum.x / points.length, y: sum.y / points.length };
  }

  function getBounds(points) {
    return points.reduce((acc, point) => ({
      minX: Math.min(acc.minX, point.x),
      minY: Math.min(acc.minY, point.y),
      maxX: Math.max(acc.maxX, point.x),
      maxY: Math.max(acc.maxY, point.y)
    }), {
      minX: Number.POSITIVE_INFINITY,
      minY: Number.POSITIVE_INFINITY,
      maxX: Number.NEGATIVE_INFINITY,
      maxY: Number.NEGATIVE_INFINITY
    });
  }

  function setViewerStatus(text) {
    dom.viewerStatus.textContent = text;
  }

  function setError(text) {
    dom.errorBox.textContent = text;
  }

  function handleError(error) {
    const payload = {
      status: error.status,
      statusText: error.statusText,
      message: error.message,
      path: error.path,
      details: error.details,
      payload: error.payload
    };
    setError(window.DebugApi.formatJson(payload));
    setViewerStatus(`Request failed: ${error.status || "network"}`);
  }

  function clamp(value, min, max) {
    return Math.max(min, Math.min(max, value));
  }

  function escapeHtml(value) {
    return String(value)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/\"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }

  init();
})();


