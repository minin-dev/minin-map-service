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
import org.mininuniver.interactiveMap.map.dto.BuildingDTO;
import org.mininuniver.interactiveMap.map.dto.BuildingShortDTO;
import org.mininuniver.interactiveMap.map.model.Building;

import java.util.List;

/**
 * The interface Building mapper.
 */
@Mapper(componentModel = "spring", uses = {FloorMapper.class})
public interface BuildingMapper {

    /**
     * To dto building dto.
     *
     * @param entity the entity
     * @return the building dto
     */
    @Mapping(target = "building", source = ".")
    BuildingDTO toDto(Building entity);

    /**
     * To short dto building short dto.
     *
     * @param entity the entity
     * @return the building short dto
     */
    BuildingShortDTO toShortDto(Building entity);

    /**
     * To entity building.
     *
     * @param dto the dto
     * @return the building
     */
    @Mapping(target = "id", source = "building.id")
    @Mapping(target = "name", source = "building.name")
    @Mapping(target = "coords", source = "building.coords")
    @Mapping(target = "floors", source = "floors")
    @Mapping(target = "version", ignore = true)
    Building toEntity(BuildingDTO dto);

    /**
     * To dto list list.
     *
     * @param entities the entities
     * @return the list
     */
    List<BuildingDTO> toDtoList(List<Building> entities);

    /**
     * To short dto list list.
     *
     * @param entities the entities
     * @return the list
     */
    List<BuildingShortDTO> toShortDtoList(List<Building> entities);

    /**
     * To entity list list.
     *
     * @param dtos the dtos
     * @return the list
     */
    List<Building> toEntityList(List<BuildingDTO> dtos);
}
