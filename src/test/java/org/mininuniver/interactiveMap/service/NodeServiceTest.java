/*
 * This file is part of mininuniver-interactive-map-service.
 *
 * Copyright (C) 2026 Eiztrips
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mininuniver.interactiveMap.dto.map.NodeDTO;
import org.mininuniver.interactiveMap.mapper.NodeMapper;
import org.mininuniver.interactiveMap.model.GraphNode;
import org.mininuniver.interactiveMap.repository.NodeRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NodeServiceTest {

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private NodeMapper nodeMapper;

    @InjectMocks
    private NodeService nodeService;

    @Test
    void getAllNodes_ok() {
        GraphNode node1 = new GraphNode();
        node1.setId(1L);
        node1.setPos(Map.of("x", 10, "y", 20));

        GraphNode node2 = new GraphNode();
        node2.setId(2L);
        node2.setPos(Map.of("x", 30, "y", 40));

        List<GraphNode> nodes = List.of(node1, node2);

        NodeDTO nodeDTO1 = new NodeDTO();
        nodeDTO1.setId(1L);
        nodeDTO1.setPos(Map.of("x", 10, "y", 20));

        NodeDTO nodeDTO2 = new NodeDTO();
        nodeDTO2.setId(2L);
        nodeDTO2.setPos(Map.of("x", 30, "y", 40));

        List<NodeDTO> dtos = List.of(nodeDTO1, nodeDTO2);

        when(nodeRepository.findAll()).thenReturn(nodes);
        when(nodeMapper.toDtoList(nodes)).thenReturn(dtos);

        List<NodeDTO> result = nodeService.getAllNodes();

        assertThat(result).hasSize(2);
        assertThat(result).isSameAs(dtos);
        verify(nodeRepository).findAll();
        verify(nodeMapper).toDtoList(nodes);
    }

    @Test
    void getAllNodes_emptyList() {
        when(nodeRepository.findAll()).thenReturn(List.of());
        when(nodeMapper.toDtoList(List.of())).thenReturn(List.of());

        List<NodeDTO> result = nodeService.getAllNodes();

        assertThat(result).isEmpty();
        verify(nodeRepository).findAll();
    }
}
