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

import lombok.RequiredArgsConstructor;
import org.mininuniver.interactiveMap.dto.map.StairsDTO;
import org.mininuniver.interactiveMap.mapper.StairsMapper;
import org.mininuniver.interactiveMap.model.Floor;
import org.mininuniver.interactiveMap.model.GraphNode;
import org.mininuniver.interactiveMap.model.Stairs;
import org.mininuniver.interactiveMap.repository.StairsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StairsService {

    private final StairsRepository stairsRepository;
    private final StairsMapper stairsMapper;

    public List<StairsDTO> getStairsByFloorId(Long floorId) {
        return stairsRepository.findByFloorId(floorId).stream()
                .map(stairsMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteAllByFloorId(Long floorId) {
        stairsRepository.deleteAllByFloorId(floorId);
    }

    @Transactional
    public void deleteAll() {
        stairsRepository.deleteAll();
    }

    @Transactional
    public void updateStairsForFloor(Floor floor, List<StairsDTO> stairsDTOs, Map<Long, Long> nodeIdMapping) {
        List<Stairs> existingStairs = stairsRepository.findByFloorId(floor.getId());
        Map<Long, Stairs> existingStairsMap = existingStairs.stream()
                .collect(Collectors.toMap(Stairs::getId, s -> s));

        for (StairsDTO stairsDTO : stairsDTOs) {
            Stairs stairs;
            if (stairsDTO.getId() != null && existingStairsMap.containsKey(stairsDTO.getId())) {
                stairs = existingStairsMap.get(stairsDTO.getId());
                existingStairsMap.remove(stairsDTO.getId());
            } else {
                stairs = new Stairs();
            }

            stairs.setFloor(floor);
            stairs.setPoints(stairsDTO.getPoints());
            stairs.setStairs(stairsDTO.getStairs());

            if (stairsDTO.getNodeId() != null) {
                Long mappedNodeId = nodeIdMapping.getOrDefault(stairsDTO.getNodeId(), stairsDTO.getNodeId());
                GraphNode node = new GraphNode();
                node.setId(mappedNodeId);
                stairs.setNode(node);
            }

            stairsRepository.save(stairs);
        }

        stairsRepository.deleteAll(existingStairsMap.values());
    }

    @Transactional
    public void createStairsForFloor(Floor floor, List<StairsDTO> stairsDTOs, Map<Long, Long> nodeIdMapping) {
        for (StairsDTO stairsDTO : stairsDTOs) {
            Stairs stairs = new Stairs();
            stairs.setFloor(floor);
            stairs.setPoints(stairsDTO.getPoints());
            stairs.setStairs(stairsDTO.getStairs());

            if (stairsDTO.getNodeId() != null) {
                Long mappedNodeId = nodeIdMapping.getOrDefault(stairsDTO.getNodeId(), stairsDTO.getNodeId());
                GraphNode node = new GraphNode();
                node.setId(mappedNodeId);
                stairs.setNode(node);
            }

            stairsRepository.save(stairs);
        }
    }
}
