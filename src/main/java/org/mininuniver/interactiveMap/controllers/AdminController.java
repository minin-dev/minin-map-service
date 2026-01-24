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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mininuniver.interactiveMap.dto.map.MapDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.mininuniver.interactiveMap.service.FloorService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin API", description = "API для администраторов")
public class AdminController {

    private final FloorService floorService;

    @Operation(summary = "Изменить/добавить данные этажа по номеру")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Этаж успешно изменен/добавлен"),
            @ApiResponse(responseCode = "404", description = "Этаж не найден", content = @Content)
    })
    @PutMapping("/floors/{number}")
    public MapDTO updateFloorData(@PathVariable int number, @RequestBody @Valid MapDTO mapDTO) {
        return floorService.updateFloorData(number, mapDTO);
    }

    @Operation(summary = "Создать новый этаж по номеру")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Этаж успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные для создания этажа", content = @Content)
    })
    @PostMapping("/floors/{number}")
    public MapDTO createFloor(@PathVariable int number, @RequestBody @Valid MapDTO mapDTO) {
        return floorService.createFloor(number, mapDTO);
    }

    @Operation(summary = "Удалить этаж по номеру")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Этаж успешно удален"),
            @ApiResponse(responseCode = "404", description = "Этаж не найден", content = @Content)
    })
    @DeleteMapping("/floors/{number}")
    public ResponseEntity<Void> deleteFloor(@PathVariable int number) {
        floorService.deleteFloor(number);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Полный сброс базы данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "База данных успешно сброшена"),
            @ApiResponse(responseCode = "500", description = "Ошибка при сбросе базы данных", content = @Content)
    })
    @DeleteMapping("/reset-db")
    public ResponseEntity<Void> resetDatabase() {
        floorService.resetDatabase();
        return ResponseEntity.noContent().build();
    }

}
