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

package org.mininuniver.interactiveMap.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.mininuniver.interactiveMap.repository.NodeRepository;
import org.mininuniver.interactiveMap.dto.map.NodeDTO;
import org.mininuniver.interactiveMap.mapper.NodeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NodeService {

    private final NodeMapper nodeMapper;
    private final NodeRepository nodeRepository;

    public NodeDTO getNodeById(Long id) {
        return nodeRepository.findById(id)
                .map(nodeMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Узел с id " + id + " не найден"));
    }

    public List<NodeDTO> getAllNodes() {
        return nodeMapper.toDtoList(nodeRepository.findAll());
    }

}