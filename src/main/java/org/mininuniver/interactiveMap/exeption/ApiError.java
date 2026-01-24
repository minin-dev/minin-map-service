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

package org.mininuniver.interactiveMap.exeption;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Модель ошибки API")
public class ApiError {
    @Schema(description = "Время возникновения ошибки", example = "2023-10-20T15:30:45.123")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP статус код", example = "404")
    private int status;

    @Schema(description = "Тип ошибки", example = "Not Found")
    private String error;

    @Schema(description = "Сообщение об ошибке", example = "Запрашиваемый ресурс не найден")
    private String message;

    @Schema(description = "URL пути, вызвавшего ошибку", example = "/api/map/floors/999")
    private String path;

    @Schema(description = "Детали ошибок валидации", nullable = true)
    private Map<String, String> validationErrors;
}