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
import org.mininuniver.interactiveMap.dto.map.NodeDTO;
import org.mininuniver.interactiveMap.model.GraphNode;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NodeMapper {
    @Mapping(source = "floor.id", target = "floorId")
    NodeDTO toDto(GraphNode entity);

    @Mapping(target = "floor.id", source = "floorId")
    GraphNode toEntity(NodeDTO dto);

    List<NodeDTO> toDtoList(List<GraphNode> entities);
    List<GraphNode> toEntityList(List<NodeDTO> dtos);
}