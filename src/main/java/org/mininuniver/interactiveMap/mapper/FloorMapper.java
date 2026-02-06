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

package org.mininuniver.interactiveMap.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mininuniver.interactiveMap.dto.map.FloorDTO;
import org.mininuniver.interactiveMap.dto.map.FloorShortDTO;
import org.mininuniver.interactiveMap.model.Floor;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FloorMapper {
    FloorDTO toDto(Floor entity);
    FloorShortDTO toShortDto(Floor entity);

    @Mapping(target = "version", ignore = true)
    Floor toEntity(FloorDTO dto);

    List<FloorDTO> toDtoList(List<Floor> entities);
    List<FloorShortDTO> toShortDtoList(List<Floor> entities);
    List<Floor> toEntityList(List<FloorDTO> dtos);
}