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

package org.mininuniver.interactiveMap.map.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mininuniver.interactiveMap.map.dto.FloorDTO;
import org.mininuniver.interactiveMap.map.dto.FloorShortDTO;
import org.mininuniver.interactiveMap.map.model.Floor;

import java.util.List;

/**
 * The interface Floor mapper.
 */
@Mapper(componentModel = "spring", uses = {RoomMapper.class, NodeMapper.class, StairsMapper.class})
public interface FloorMapper {
    /**
     * To dto floor dto.
     *
     * @param entity the entity
     * @return the floor dto
     */
    @Mapping(target = "floor", source = ".")
    FloorDTO toDto(Floor entity);

    /**
     * To short dto floor short dto.
     *
     * @param entity the entity
     * @return the floor short dto
     */
    @Mapping(source = "building.id", target = "buildingId")
    FloorShortDTO toShortDto(Floor entity);

    /**
     * To entity floor.
     *
     * @param dto the dto
     * @return the floor
     */
    @Mapping(target = "id", source = "floor.id")
    @Mapping(target = "number", source = "floor.number")
    @Mapping(target = "name", source = "floor.name")
    @Mapping(target = "building.id", source = "floor.buildingId")
    @Mapping(target = "points", source = "floor.points")
    @Mapping(target = "version", ignore = true)
    Floor toEntity(FloorDTO dto);

    /**
     * To dto list list.
     *
     * @param entities the entities
     * @return the list
     */
    List<FloorDTO> toDtoList(List<Floor> entities);

    /**
     * To short dto list list.
     *
     * @param entities the entities
     * @return the list
     */
    List<FloorShortDTO> toShortDtoList(List<Floor> entities);

    /**
     * To entity list list.
     *
     * @param dtos the dtos
     * @return the list
     */
    List<Floor> toEntityList(List<FloorDTO> dtos);
}