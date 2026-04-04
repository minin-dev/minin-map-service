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

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mininuniver.interactiveMap.map.controller.MapController;
import org.mininuniver.interactiveMap.map.dto.*;
import org.mininuniver.interactiveMap.auth.security.JwtUtil;
import org.mininuniver.interactiveMap.map.service.BuildingService;
import org.mininuniver.interactiveMap.map.service.FloorService;
import org.mininuniver.interactiveMap.map.service.NodeService;
import org.mininuniver.interactiveMap.map.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MapController.class)
public class MapControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FloorService floorService;

    @MockitoBean
    private BuildingService buildingService;

    @MockitoBean
    private RoomService roomService;

    @MockitoBean
    private NodeService nodeService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

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
    @WithMockUser
    void getFloorById_ok() throws Exception {
        FloorShortDTO floorShortDTO = new FloorShortDTO();
        floorShortDTO.setId(1L);
        floorShortDTO.setNumber(1);
        floorShortDTO.setName("Первый этаж");
        floorShortDTO.setPoints(createPoints());

        FloorDTO mapDTO = new FloorDTO(floorShortDTO, List.of(), List.of(), List.of());
        when(floorService.getFloorById(1L)).thenReturn(mapDTO);

        mockMvc.perform(get("/api/v1/map/floors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.floor.id").value(1))
                .andExpect(jsonPath("$.floor.name").value("Первый этаж"));
    }

    @Test
    @WithMockUser
    void getFloorById_notFound() throws Exception {
        when(floorService.getFloorById(99L)).thenThrow(new EntityNotFoundException("Этаж не найден"));

        mockMvc.perform(get("/api/v1/map/floors/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void searchFloors_ok() throws Exception {
        FloorShortDTO floor1 = new FloorShortDTO();
        floor1.setId(1L);
        floor1.setNumber(1);
        floor1.setName("Первый этаж");

        FloorShortDTO floor2 = new FloorShortDTO();
        floor2.setId(2L);
        floor2.setNumber(2);
        floor2.setName("Второй этаж");

        when(floorService.searchFloors(1L)).thenReturn(List.of(floor1, floor2));

        mockMvc.perform(get("/api/v1/map/floors/search")
                        .param("buildingId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].number").value(1))
                .andExpect(jsonPath("$[1].number").value(2));
    }

    @Test
    @WithMockUser
    void searchRoomsByBuildingAndName_ok() throws Exception {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1L);
        roomDTO.setName("A101");
        roomDTO.setFloorId(1L);
        roomDTO.setPoints(createPoints());

        when(roomService.searchRooms(1L, null, "A101")).thenReturn(List.of(roomDTO));

        mockMvc.perform(get("/api/v1/map/rooms/search")
                        .param("buildingId", "1")
                        .param("name", "A101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("A101"));
    }

    @Test
    @WithMockUser
    void searchRoomsByBuildingAndName_notFound() throws Exception {
        when(roomService.searchRooms(1L, null, "unknown"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/map/rooms/search")
                        .param("buildingId", "1")
                        .param("name", "unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser
    void searchRoomsByBuildingAndFloorAndName_ok() throws Exception {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1L);
        roomDTO.setName("A101");
        roomDTO.setFloorId(1L);
        roomDTO.setPoints(createPoints());

        when(roomService.searchRooms(1L, 1, "A101")).thenReturn(List.of(roomDTO));

        mockMvc.perform(get("/api/v1/map/rooms/search")
                        .param("buildingId", "1")
                        .param("floor", "1")
                        .param("name", "A101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("A101"));
    }

    @Test
    @WithMockUser
    void searchRoomsByBuilding_ok() throws Exception {
        RoomDTO room1 = new RoomDTO();
        room1.setId(1L);
        room1.setName("A101");

        RoomDTO room2 = new RoomDTO();
        room2.setId(2L);
        room2.setName("A102");

        when(roomService.searchRooms(1L, null, null)).thenReturn(List.of(room1, room2));

        mockMvc.perform(get("/api/v1/map/rooms/search")
                        .param("buildingId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser
    void searchAllRooms_ok() throws Exception {
        RoomDTO room1 = new RoomDTO();
        room1.setId(1L);
        room1.setName("A101");

        RoomDTO room2 = new RoomDTO();
        room2.setId(2L);
        room2.setName("A102");

        when(roomService.searchRooms(null, null, null)).thenReturn(List.of(room1, room2));

        mockMvc.perform(get("/api/v1/map/rooms/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser
    void getNodeById_ok() throws Exception {
        NodeDTO nodeDTO = new NodeDTO();
        nodeDTO.setId(1L);
        nodeDTO.setFloorId(1L);
        nodeDTO.setPos(Map.of("x", 10, "y", 20));

        when(nodeService.getNodeById(1L)).thenReturn(nodeDTO);

        mockMvc.perform(get("/api/v1/map/nodes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.pos.x").value(10));
    }

    @Test
    @WithMockUser
    void getNodeById_notFound() throws Exception {
        when(nodeService.getNodeById(99L)).thenThrow(new EntityNotFoundException("Узел не найден"));

        mockMvc.perform(get("/api/v1/map/nodes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void searchNodes_ok() throws Exception {
        NodeDTO node1 = new NodeDTO();
        node1.setId(1L);
        node1.setFloorId(1L);
        node1.setPos(Map.of("x", 10, "y", 20));

        NodeDTO node2 = new NodeDTO();
        node2.setId(2L);
        node2.setFloorId(1L);
        node2.setPos(Map.of("x", 30, "y", 40));

        when(nodeService.getAllNodes()).thenReturn(List.of(node1, node2));

        mockMvc.perform(get("/api/v1/map/nodes/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getFloorById_unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/map/floors/1"))
                .andExpect(status().isUnauthorized());
    }
}
