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

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mininuniver.interactiveMap.dto.map.*;
import org.mininuniver.interactiveMap.security.JwtUtil;
import org.mininuniver.interactiveMap.service.BuildingService;
import org.mininuniver.interactiveMap.service.FloorService;
import org.mininuniver.interactiveMap.service.NodeService;
import org.mininuniver.interactiveMap.service.RoomService;
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

    @Autowired
    private ObjectMapper objectMapper;

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
    void getFloorByNumber_ok() throws Exception {
        FloorShortDTO floorShortDTO = new FloorShortDTO();
        floorShortDTO.setId(1L);
        floorShortDTO.setNumber(1);
        floorShortDTO.setName("Первый этаж");
        floorShortDTO.setPoints(createPoints());

        FloorDTO mapDTO = new FloorDTO(floorShortDTO, List.of(), List.of(), List.of());
        when(floorService.getFloorDataByBuildingIdAndNumber(1L, 1)).thenReturn(mapDTO);

        mockMvc.perform(get("/api/v1/map/buildings/1/floors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.floor.id").value(1))
                .andExpect(jsonPath("$.floor.name").value("Первый этаж"));
    }

    @Test
    @WithMockUser
    void getFloorByNumber_notFound() throws Exception {
        when(floorService.getFloorDataByBuildingIdAndNumber(1L, 99)).thenThrow(new EntityNotFoundException("Этаж не найден"));

        mockMvc.perform(get("/api/v1/map/buildings/1/floors/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getAllFloors_ok() throws Exception {
        FloorShortDTO floor1 = new FloorShortDTO();
        floor1.setId(1L);
        floor1.setNumber(1);
        floor1.setName("Первый этаж");

        FloorShortDTO floor2 = new FloorShortDTO();
        floor2.setId(2L);
        floor2.setNumber(2);
        floor2.setName("Второй этаж");

        when(floorService.getFloorsByBuildingId(1L)).thenReturn(List.of(floor1, floor2));

        mockMvc.perform(get("/api/v1/map/buildings/1/floors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].number").value(1))
                .andExpect(jsonPath("$[1].number").value(2));
    }

    @Test
    @WithMockUser
    void getRoomByName_ok() throws Exception {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1L);
        roomDTO.setName("A101");
        roomDTO.setFloorId(1L);
        roomDTO.setPoints(createPoints());

        when(roomService.getRoomByName("A101")).thenReturn(roomDTO);

        mockMvc.perform(get("/api/v1/map/rooms/A101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("A101"));
    }

    @Test
    @WithMockUser
    void getRoomByName_notFound() throws Exception {
        when(roomService.getRoomByName("unknown")).thenThrow(new EntityNotFoundException("Помещение unknown не найдено"));

        mockMvc.perform(get("/api/v1/map/rooms/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getAllRooms_ok() throws Exception {
        RoomDTO room1 = new RoomDTO();
        room1.setId(1L);
        room1.setName("A101");

        RoomDTO room2 = new RoomDTO();
        room2.setId(2L);
        room2.setName("A102");

        when(roomService.getAllRooms()).thenReturn(List.of(room1, room2));

        mockMvc.perform(get("/api/v1/map/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser
    void getAllNodes_ok() throws Exception {
        NodeDTO node1 = new NodeDTO();
        node1.setId(1L);
        node1.setFloorId(1L);
        node1.setPos(Map.of("x", 10, "y", 20));

        NodeDTO node2 = new NodeDTO();
        node2.setId(2L);
        node2.setFloorId(1L);
        node2.setPos(Map.of("x", 30, "y", 40));

        when(nodeService.getAllNodes()).thenReturn(List.of(node1, node2));

        mockMvc.perform(get("/api/v1/map/nodes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getFloorByNumber_unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/map/buildings/1/floors/1"))
                .andExpect(status().isUnauthorized());
    }
}
