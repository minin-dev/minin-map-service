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

package org.mininuniver.interactiveMap.map.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * The type Point dto.
 */
@Data
public class PointDTO {
    @NotNull(message = "Координата X обязательна")
    private int x;
    @NotNull(message = "Координата Y обязательна")
    private int y;

    /**
     * Instantiates a new Point dto.
     */
    public PointDTO() {}

    /**
     * Instantiates a new Point dto.
     *
     * @param x the x
     * @param y the y
     */
    public PointDTO(int x, int y) {
        this.x = x;
        this.y = y;
    }
}