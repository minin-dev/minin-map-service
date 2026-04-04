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

package org.mininuniver.interactiveMap.map.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.mininuniver.interactiveMap.map.repository.NodeRepository;
import org.mininuniver.interactiveMap.map.dto.NodeDTO;
import org.mininuniver.interactiveMap.map.mapper.NodeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import org.mininuniver.interactiveMap.map.model.Floor;
import org.mininuniver.interactiveMap.map.model.GraphNode;

@Service
@RequiredArgsConstructor
public class NodeService {

    private final NodeMapper nodeMapper;
    private final NodeRepository nodeRepository;

    @Transactional
    public void deleteAllByFloorId(Long floorId) {
        nodeRepository.deleteAllByFloorId(floorId);
    }

    @Transactional
    public void deleteAll() {
        nodeRepository.deleteAll();
    }

    public List<NodeDTO> getNodesByFloorId(Long floorId) {
        return nodeRepository.findByFloorId(floorId).stream()
                .map(nodeMapper::toDto)
                .toList();
    }

    @Transactional
    public Map<Long, Long> updateNodesForFloor(Floor floor, List<NodeDTO> nodeDTOs) {
        List<GraphNode> existingNodes = nodeRepository.findByFloorId(floor.getId());
        Map<Long, GraphNode> existingNodesMap = existingNodes.stream()
                .collect(Collectors.toMap(GraphNode::getId, n -> n));

        Map<Long, Long> nodeIdMapping = new HashMap<>();

        List<GraphNode> updatedNodes = new ArrayList<>();
        for (NodeDTO nodeDTO : nodeDTOs) {
            GraphNode node;
            if (nodeDTO.getId() != null && existingNodesMap.containsKey(nodeDTO.getId())) {
                node = existingNodesMap.get(nodeDTO.getId());
                node.setPos(nodeDTO.getPos());
                existingNodesMap.remove(nodeDTO.getId());
            } else {
                node = new GraphNode();
                node.setPos(nodeDTO.getPos());
                node.setFloor(floor);
            }

            node = nodeRepository.save(node);

            Long oldId = nodeDTO.getId() != null ? nodeDTO.getId() : -node.getId();
            nodeIdMapping.put(oldId, node.getId());
            updatedNodes.add(node);
        }

        nodeRepository.deleteAll(existingNodesMap.values());

        for (int i = 0; i < nodeDTOs.size(); i++) {
            NodeDTO nodeDTO = nodeDTOs.get(i);
            GraphNode node = updatedNodes.get(i);

            if (nodeDTO.getNeighbors() != null) {
                Long[] newNeighbors = Arrays.stream(nodeDTO.getNeighbors())
                        .map(n -> nodeIdMapping.getOrDefault(n, n))
                        .toArray(Long[]::new);
                node.setNeighbors(newNeighbors);
                nodeRepository.save(node);
            }
        }
        return nodeIdMapping;
    }

    @Transactional
    public Map<Long, Long> createNodesForFloor(Floor floor, List<NodeDTO> nodeDTOs) {
        Map<Long, Long> nodeIdMapping = new HashMap<>();

        for (NodeDTO nodeDTO : nodeDTOs) {
            GraphNode node = new GraphNode();
            node.setPos(nodeDTO.getPos());
            node.setFloor(floor);
            node = nodeRepository.save(node);

            Long oldId = nodeDTO.getId() != null ? nodeDTO.getId() : -node.getId();
            nodeIdMapping.put(oldId, node.getId());
        }
        return nodeIdMapping;
    }

    public NodeDTO getNodeById(Long id) {
        return nodeRepository.findById(id)
                .map(nodeMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Узел с id " + id + " не найден"));
    }

    public List<NodeDTO> getAllNodes() {
        return nodeMapper.toDtoList(nodeRepository.findAll());
    }

}