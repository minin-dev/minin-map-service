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

package org.mininuniver.interactiveMap.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.mininuniver.interactiveMap.dto.map.FloorDTO;
import org.mininuniver.interactiveMap.dto.map.FloorShortDTO;
import org.mininuniver.interactiveMap.dto.map.NodeDTO;
import org.mininuniver.interactiveMap.dto.map.RoomDTO;
import org.mininuniver.interactiveMap.dto.map.BuildingDTO;
import org.mininuniver.interactiveMap.dto.map.BuildingShortDTO;
import org.mininuniver.interactiveMap.model.GraphNode;
import org.mininuniver.interactiveMap.model.Room;
import org.mininuniver.interactiveMap.service.FloorService;
import org.mininuniver.interactiveMap.service.NodeService;
import org.mininuniver.interactiveMap.service.RoomService;
import org.mininuniver.interactiveMap.service.BuildingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base.path}/map")
@RequiredArgsConstructor
@Tag(name = "Map API", description = "API для работы с картой здания")
public class MapController {

    private final NodeService nodeService;
    private final RoomService roomService;
    private final FloorService floorService;
    private final BuildingService buildingService;

    @Operation(summary = "Получить все здания")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Здания найдены",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BuildingShortDTO.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content)
    })
    @GetMapping("/buildings")
    public List<BuildingShortDTO> getAllBuildings() {
        return buildingService.getAllBuildings();
    }

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

    @Operation(summary = "Получить данные этажа по номеру и id здания")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Этаж найден",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = FloorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Ошибка запроса", content = @Content),
            @ApiResponse(responseCode = "404", description = "Этаж не найден", content = @Content),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content)
    })
    @GetMapping("/buildings/{buildingId}/floors/{number}")
    public FloorDTO getFloorByBuildingAndNumber(@PathVariable Long buildingId, @PathVariable @Min(0) int number) {
        return floorService.getFloorDataByBuildingIdAndNumber(buildingId, number);
    }

    @Operation(summary = "Получить все объекты этажей здания (без Points)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Этажи найдены",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = FloorShortDTO.class))),
            @ApiResponse(responseCode = "403", description = "Ошибка запроса", content = @Content),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content)
    })
    @GetMapping("/buildings/{buildingId}/floors")
    public List<FloorShortDTO> getFloorsByBuilding(@PathVariable Long buildingId) {
        return floorService.getFloorsByBuildingId(buildingId);
    }

    @Operation(summary = "Получить комнату по имени")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комната найдена",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Room.class))),
            @ApiResponse(responseCode = "403", description = "Ошибка запроса", content = @Content),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content)
    })
    @GetMapping("/rooms/{name}")
    public RoomDTO getRoomByName(@PathVariable String name) {
        return roomService.getRoomByName(name);
    }

    @Operation(summary = "Получить все комнаты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комнаты найдены",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Room.class))),
            @ApiResponse(responseCode = "403", description = "Ошибка запроса", content = @Content),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content)
    })
    @GetMapping("/rooms")
    public List<RoomDTO> getAllRooms() {
        return roomService.getAllRooms();
    }

    @Operation(summary = "Получить все узлы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Узлы найдены",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GraphNode.class))),
            @ApiResponse(responseCode = "403", description = "Ошибка запроса", content = @Content),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content)
    })
    @GetMapping("/nodes")
    public List<NodeDTO> getAllNodes() {
        return nodeService.getAllNodes();
    }

}
