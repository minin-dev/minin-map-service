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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mininuniver.interactiveMap.map.dto.FloorDTO;
import org.mininuniver.interactiveMap.map.dto.BuildingShortDTO;
import org.mininuniver.interactiveMap.map.service.DBService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.mininuniver.interactiveMap.map.service.FloorService;
import org.mininuniver.interactiveMap.map.service.BuildingService;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.base.path}/admin")
@Tag(name = "Admin API", description = "API для администраторов")
public class AdminController {

    private final FloorService floorService;
    private final BuildingService buildingService;
    private final DBService dbService;

    @Operation(summary = "Создать новое здание")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Здание успешно создано"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content)
    })
    @PostMapping("/buildings")
    public BuildingShortDTO createBuilding(@RequestBody @Valid BuildingShortDTO buildingDTO) {
        return buildingService.createBuilding(buildingDTO);
    }

    @Operation(summary = "Обновить здание")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Здание успешно обновлено"),
            @ApiResponse(responseCode = "404", description = "Здание не найдено", content = @Content)
    })
    @PutMapping("/buildings/{id}")
    public BuildingShortDTO updateBuilding(@PathVariable Long id, @RequestBody @Valid BuildingShortDTO buildingDTO) {
        return buildingService.updateBuilding(id, buildingDTO);
    }

    @Operation(summary = "Удалить здание")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Здание успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Здание не найдено", content = @Content)
    })
    @DeleteMapping("/buildings/{id}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Изменить/добавить данные этажа по номеру и id здания")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Этаж успешно изменен/добавлен"),
            @ApiResponse(responseCode = "404", description = "Этаж не найден", content = @Content)
    })
    @PutMapping("/buildings/{buildingId}/floors/{number}")
    public FloorDTO updateFloorData(@PathVariable Long buildingId, @PathVariable int number, @RequestBody @Valid FloorDTO mapDTO) {
        return floorService.updateFloorData(buildingId, number, mapDTO);
    }

    @Operation(summary = "Создать новый этаж по номеру и id здания")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Этаж успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные для создания этажа", content = @Content)
    })
    @PostMapping("/buildings/{buildingId}/floors/{number}")
    public FloorDTO createFloor(@PathVariable Long buildingId, @PathVariable int number, @RequestBody @Valid FloorDTO mapDTO) {
        return floorService.createFloor(buildingId, number, mapDTO);
    }

    @Operation(summary = "Удалить этаж по номеру и id здания")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Этаж успешно удален"),
            @ApiResponse(responseCode = "404", description = "Этаж не найден", content = @Content)
    })
    @DeleteMapping("/buildings/{buildingId}/floors/{number}")
    public ResponseEntity<Void> deleteFloor(@PathVariable Long buildingId, @PathVariable int number) {
        floorService.deleteFloor(buildingId, number);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Полный сброс базы данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "База данных успешно сброшена"),
            @ApiResponse(responseCode = "500", description = "Ошибка при сбросе базы данных", content = @Content)
    })
    @DeleteMapping("/reset-db")
    public ResponseEntity<Void> resetDatabase() {
        dbService.resetDatabase();
        return ResponseEntity.noContent().build();
    }

}
