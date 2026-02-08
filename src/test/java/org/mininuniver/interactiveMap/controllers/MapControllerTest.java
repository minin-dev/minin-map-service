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
import org.mininuniver.interactiveMap.service.BuildingService;
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

    @Mock
    private BuildingService buildingService;

    @InjectMocks
    private MapController mapController;

    private FloorShortDTO floorShortDTO;
    private RoomDTO roomDTO;
    private NodeDTO nodeDTO;
    private FloorDTO mapDTO;

    @BeforeEach
    void setUp() {
        floorShortDTO = new FloorShortDTO();
        floorShortDTO.setId(1L);
        floorShortDTO.setNumber(1);
        floorShortDTO.setName("Первый этаж");
        floorShortDTO.setPoints(createPoints());

        roomDTO = new RoomDTO();
        roomDTO.setId(1L);
        roomDTO.setName("A101");
        roomDTO.setFloorId(1L);
        roomDTO.setPoints(createPoints());

        nodeDTO = new NodeDTO();
        nodeDTO.setId(1L);
        nodeDTO.setFloorId(1L);
        nodeDTO.setPos(Map.of("x", 10, "y", 20));

        mapDTO = new FloorDTO(floorShortDTO, List.of(roomDTO), List.of(), List.of(nodeDTO));
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
    void getFloorById_ok() {
        when(floorService.getFloorById(1L)).thenReturn(mapDTO);

        FloorDTO result = mapController.getFloorById(1L);

        assertThat(result).isEqualTo(mapDTO);
        verify(floorService).getFloorById(1L);
    }

    @Test
    void searchFloors_ok() {
        FloorShortDTO floor2 = new FloorShortDTO();
        floor2.setId(2L);
        floor2.setNumber(2);
        floor2.setName("Второй этаж");

        List<FloorShortDTO> floors = List.of(floorShortDTO, floor2);
        when(floorService.searchFloors(1L)).thenReturn(floors);

        List<FloorShortDTO> result = mapController.searchFloors(1L);

        assertThat(result).hasSize(2);
        verify(floorService).searchFloors(1L);
    }

    @Test
    void searchRoomsByBuildingAndName_ok() {
        when(roomService.searchRooms(1L, null, "A101")).thenReturn(List.of(roomDTO));

        List<RoomDTO> result = mapController.searchRooms(1L, null, "A101");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("A101");
        verify(roomService).searchRooms(1L, null, "A101");
    }

    @Test
    void searchRoomsByBuildingAndFloorAndName_ok() {
        when(roomService.searchRooms(1L, 1, "A101")).thenReturn(List.of(roomDTO));

        List<RoomDTO> result = mapController.searchRooms(1L, 1, "A101");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("A101");
        verify(roomService).searchRooms(1L, 1, "A101");
    }

    @Test
    void searchRoomsByBuilding_ok() {
        RoomDTO room2 = new RoomDTO();
        room2.setId(2L);
        room2.setName("A102");
        room2.setFloorId(1L);

        List<RoomDTO> rooms = List.of(roomDTO, room2);
        when(roomService.searchRooms(1L, null, null)).thenReturn(rooms);

        List<RoomDTO> result = mapController.searchRooms(1L, null, null);

        assertThat(result).hasSize(2);
        verify(roomService).searchRooms(1L, null, null);
    }

    @Test
    void searchRoomsByBuildingAndFloor_ok() {
        RoomDTO room2 = new RoomDTO();
        room2.setId(2L);
        room2.setName("A102");
        room2.setFloorId(1L);

        List<RoomDTO> rooms = List.of(roomDTO, room2);
        when(roomService.searchRooms(1L, 1, null)).thenReturn(rooms);

        List<RoomDTO> result = mapController.searchRooms(1L, 1, null);

        assertThat(result).hasSize(2);
        verify(roomService).searchRooms(1L, 1, null);
    }

    @Test
    void getRoomById_ok() {
        when(roomService.getRoomById(1L)).thenReturn(roomDTO);

        RoomDTO result = mapController.getRoomById(1L);

        assertThat(result).isEqualTo(roomDTO);
        verify(roomService).getRoomById(1L);
    }

    @Test
    void searchAllRooms_ok() {
        RoomDTO room2 = new RoomDTO();
        room2.setId(2L);
        room2.setName("A102");

        List<RoomDTO> rooms = List.of(roomDTO, room2);
        when(roomService.searchRooms(null, null, null)).thenReturn(rooms);

        List<RoomDTO> result = mapController.searchRooms(null, null, null);

        assertThat(result).hasSize(2);
        verify(roomService).searchRooms(null, null, null);
    }

    @Test
    void getNodeById_ok() {
        when(nodeService.getNodeById(1L)).thenReturn(nodeDTO);

        NodeDTO result = mapController.getNodeById(1L);

        assertThat(result).isEqualTo(nodeDTO);
        verify(nodeService).getNodeById(1L);
    }

    @Test
    void searchNodes_ok() {
        NodeDTO node2 = new NodeDTO();
        node2.setId(2L);
        node2.setPos(Map.of("x", 30, "y", 40));

        List<NodeDTO> nodes = List.of(nodeDTO, node2);
        when(nodeService.getAllNodes()).thenReturn(nodes);

        List<NodeDTO> result = mapController.searchNodes();

        assertThat(result).hasSize(2);
        verify(nodeService).getAllNodes();
    }
}
