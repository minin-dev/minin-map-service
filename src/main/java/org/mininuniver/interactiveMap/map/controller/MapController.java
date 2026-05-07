/*
 * This file is part of mininuniver-interactive-map-service.
 *
 * Copyright (C) 2025 Eiztrips
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.mininuniver.interactiveMap.map.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.mininuniver.interactiveMap.map.dto.FloorDTO;
import org.mininuniver.interactiveMap.map.dto.FloorShortDTO;
import org.mininuniver.interactiveMap.map.dto.NodeDTO;
import org.mininuniver.interactiveMap.map.dto.RoomDTO;
import org.mininuniver.interactiveMap.map.dto.BuildingDTO;
import org.mininuniver.interactiveMap.map.dto.BuildingShortDTO;
import org.mininuniver.interactiveMap.map.service.FloorService;
import org.mininuniver.interactiveMap.map.service.NodeService;
import org.mininuniver.interactiveMap.map.service.RoomService;
import org.mininuniver.interactiveMap.map.service.BuildingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The type Map controller.
 */
@RestController
@RequestMapping("${api.base.path}/map")
@RequiredArgsConstructor
@Tag(name = "Map API", description = "API для работы с картой здания")
public class MapController {

    private final NodeService nodeService;
    private final RoomService roomService;
    private final FloorService floorService;
    private final BuildingService buildingService;

    // --- Building Endpoints ---

    /**
     * Search buildings list.
     *
     * @return the list
     */
    @Operation(summary = "Получить все здания (поиск)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Здания найдены",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BuildingShortDTO.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content)
    })
    @GetMapping("/buildings/search")
    public List<BuildingShortDTO> searchBuildings() {
        return buildingService.getAllBuildings();
    }

    /**
     * Gets building by id.
     *
     * @param id the id
     * @return the building by id
     */
    @Operation(summary = "Получить здание по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Здание найдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BuildingDTO.class))),
            @ApiResponse(responseCode = "404", description = "Здание не найдено", content = @Content),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content)
    })
    @GetMapping("/buildings/{id}")
    public BuildingDTO getBuildingById(@PathVariable Long id) {
        return buildingService.getBuildingById(id);
    }

    // --- Floor Endpoints ---

    /**
     * Search floors list.
     *
     * @param buildingId the building id
     * @return the list
     */
    @Operation(summary = "Поиск этажей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Этажи найдены",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = FloorShortDTO.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content)
    })
    @GetMapping("/floors/search")
    public List<FloorShortDTO> searchFloors(@RequestParam(required = false) Long buildingId) {
        return floorService.searchFloors(buildingId);
    }

    /**
     * Gets floor by id.
     *
     * @param id the id
     * @return the floor by id
     */
    @Operation(summary = "Получить данные этажа по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Этаж найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FloorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Этаж не найден", content = @Content),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content)
    })
    @GetMapping("/floors/{id}")
    public FloorDTO getFloorById(@PathVariable Long id) {
        return floorService.getFloorById(id);
    }

    // --- Room Endpoints ---

    /**
     * Search rooms list.
     *
     * @param buildingId the building id
     * @param floor      the floor
     * @param name       the name
     * @return the list
     */
    @Operation(summary = "Поиск комнат")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комнаты найдены",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoomDTO.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content)
    })
    @GetMapping("/rooms/search")
    public List<RoomDTO> searchRooms(@RequestParam(required = false) Long buildingId,
                                     @RequestParam(required = false) Integer floor,
                                     @RequestParam(required = false) String name) {
        return roomService.searchRooms(buildingId, floor, name);
    }

    /**
     * Gets room by id.
     *
     * @param id the id
     * @return the room by id
     */
    @Operation(summary = "Получить комнату по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комната найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoomDTO.class))),
            @ApiResponse(responseCode = "404", description = "Комната не найдена", content = @Content),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content)
    })
    @GetMapping("/rooms/{id}")
    public RoomDTO getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    // --- Node Endpoints ---

    /**
     * Search nodes list.
     *
     * @return the list
     */
    @Operation(summary = "Получить все узлы (поиск)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Узлы найдены",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = NodeDTO.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content)
    })
    @GetMapping("/nodes/search")
    public List<NodeDTO> searchNodes() {
        return nodeService.getAllNodes();
    }

    /**
     * Gets node by id.
     *
     * @param id the id
     * @return the node by id
     */
    @Operation(summary = "Получить узел по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Узел найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NodeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Узел не найден", content = @Content),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content)
    })
    @GetMapping("/nodes/{id}")
    public NodeDTO getNodeById(@PathVariable Long id) {
        return nodeService.getNodeById(id);
    }

}
