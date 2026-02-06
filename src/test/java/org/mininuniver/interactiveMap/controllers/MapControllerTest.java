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

package org.mininuniver.interactiveMap.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mininuniver.interactiveMap.dto.map.*;
import org.mininuniver.interactiveMap.service.FloorService;
import org.mininuniver.interactiveMap.service.NodeService;
import org.mininuniver.interactiveMap.service.RoomService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MapControllerTest {

    @Mock
    private NodeService nodeService;

    @Mock
    private RoomService roomService;

    @Mock
    private FloorService floorService;

    @InjectMocks
    private MapController mapController;

    private FloorDTO floorDTO;
    private FloorShortDTO floorShortDTO;
    private RoomDTO roomDTO;
    private NodeDTO nodeDTO;
    private MapDTO mapDTO;

    @BeforeEach
    void setUp() {
        floorDTO = new FloorDTO();
        floorDTO.setId(1L);
        floorDTO.setNumber(1);
        floorDTO.setName("Первый этаж");
        floorDTO.setPoints(createPoints());

        floorShortDTO = new FloorShortDTO();
        floorShortDTO.setId(1L);
        floorShortDTO.setNumber(1);
        floorShortDTO.setName("Первый этаж");

        roomDTO = new RoomDTO();
        roomDTO.setId(1L);
        roomDTO.setName("A101");
        roomDTO.setFloorId(1L);
        roomDTO.setPoints(createPoints());

        nodeDTO = new NodeDTO();
        nodeDTO.setId(1L);
        nodeDTO.setFloorId(1L);
        nodeDTO.setPos(Map.of("x", 10, "y", 20));

        mapDTO = new MapDTO(floorDTO, List.of(roomDTO), List.of(), List.of(nodeDTO));
    }

    private List<PointDTO> createPoints() {
        List<PointDTO> points = new ArrayList<>();
        PointDTO p1 = new PointDTO();
        p1.setX(0);
        p1.setY(0);
        PointDTO p2 = new PointDTO();
        p2.setX(100);
        p2.setY(0);
        PointDTO p3 = new PointDTO();
        p3.setX(100);
        p3.setY(100);
        points.add(p1);
        points.add(p2);
        points.add(p3);
        return points;
    }

    @Test
    void getFloorByNumber_ok() {
        when(floorService.getMapData(1)).thenReturn(mapDTO);

        MapDTO result = mapController.getFloorByNumber(1);

        assertThat(result).isEqualTo(mapDTO);
        verify(floorService).getMapData(1);
    }

    @Test
    void getAllFloors_ok() {
        FloorShortDTO floor2 = new FloorShortDTO();
        floor2.setId(2L);
        floor2.setNumber(2);
        floor2.setName("Второй этаж");

        List<FloorShortDTO> floors = List.of(floorShortDTO, floor2);
        when(floorService.getAllFloors()).thenReturn(floors);

        List<FloorShortDTO> result = mapController.getAllFloors();

        assertThat(result).hasSize(2);
        verify(floorService).getAllFloors();
    }

    @Test
    void getRoomByName_ok() {
        when(roomService.getRoomByName("A101")).thenReturn(roomDTO);

        RoomDTO result = mapController.getRoomByName("A101");

        assertThat(result).isEqualTo(roomDTO);
        assertThat(result.getName()).isEqualTo("A101");
        verify(roomService).getRoomByName("A101");
    }

    @Test
    void getAllRooms_ok() {
        RoomDTO room2 = new RoomDTO();
        room2.setId(2L);
        room2.setName("A102");

        List<RoomDTO> rooms = List.of(roomDTO, room2);
        when(roomService.getAllRooms()).thenReturn(rooms);

        List<RoomDTO> result = mapController.getAllRooms();

        assertThat(result).hasSize(2);
        verify(roomService).getAllRooms();
    }

    @Test
    void getAllNodes_ok() {
        NodeDTO node2 = new NodeDTO();
        node2.setId(2L);
        node2.setPos(Map.of("x", 30, "y", 40));

        List<NodeDTO> nodes = List.of(nodeDTO, node2);
        when(nodeService.getAllNodes()).thenReturn(nodes);

        List<NodeDTO> result = mapController.getAllNodes();

        assertThat(result).hasSize(2);
        verify(nodeService).getAllNodes();
    }
}
