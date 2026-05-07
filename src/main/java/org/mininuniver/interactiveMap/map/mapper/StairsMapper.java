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
import org.mininuniver.interactiveMap.map.dto.StairsDTO;
import org.mininuniver.interactiveMap.map.model.Stairs;

import java.util.List;

/**
 * The interface Stairs mapper.
 */
@Mapper(componentModel = "spring")
public interface StairsMapper {
    /**
     * To dto stairs dto.
     *
     * @param entity the entity
     * @return the stairs dto
     */
    @Mapping(source = "node.id", target = "nodeId")
    @Mapping(source = "floor.id", target = "floorId")
    StairsDTO toDto(Stairs entity);

    /**
     * To entity stairs.
     *
     * @param dto the dto
     * @return the stairs
     */
    @Mapping(target = "node.id", source = "nodeId")
    @Mapping(target = "floor.id", source = "floorId")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "name", ignore = true)
    Stairs toEntity(StairsDTO dto);

    /**
     * To dto list list.
     *
     * @param entities the entities
     * @return the list
     */
    List<StairsDTO> toDtoList(List<Stairs> entities);

    /**
     * To entity list list.
     *
     * @param dtos the dtos
     * @return the list
     */
    List<Stairs> toEntityList(List<StairsDTO> dtos);
}