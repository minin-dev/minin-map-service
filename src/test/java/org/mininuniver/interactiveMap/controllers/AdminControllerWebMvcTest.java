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
import org.junit.jupiter.api.Test;
import org.mininuniver.interactiveMap.map.controller.AdminController;
import org.mininuniver.interactiveMap.map.dto.FloorDTO;
import org.mininuniver.interactiveMap.map.dto.FloorShortDTO;
import org.mininuniver.interactiveMap.map.dto.PointDTO;
import org.mininuniver.interactiveMap.auth.security.JwtUtil;
import org.mininuniver.interactiveMap.map.service.BuildingService;
import org.mininuniver.interactiveMap.map.service.DBService;
import org.mininuniver.interactiveMap.map.service.FloorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FloorService floorService;

    @MockitoBean
    private BuildingService buildingService;

    @MockitoBean
    private DBService dbService;

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

    private FloorDTO createMapDTO() {
        FloorShortDTO floorShortDTO = new FloorShortDTO();
        floorShortDTO.setId(1L);
        floorShortDTO.setNumber(1);
        floorShortDTO.setName("Первый этаж");
        floorShortDTO.setPoints(createPoints());
        return new FloorDTO(floorShortDTO, List.of(), List.of(), List.of());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateFloorData_ok() throws Exception {
        FloorShortDTO floorShortDTO = new FloorShortDTO();
        floorShortDTO.setId(1L);
        floorShortDTO.setNumber(1);
        floorShortDTO.setName("Первый этаж");
        floorShortDTO.setPoints(createPoints());

        FloorDTO mapDTO = new FloorDTO(floorShortDTO, List.of(), List.of(), List.of());

        when(floorService.updateFloorData(eq(1L), eq(1), any(FloorDTO.class))).thenReturn(mapDTO);

        mockMvc.perform(put("/api/v1/admin/buildings/1/floors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mapDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.floor.name").value("Первый этаж"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createFloor_ok() throws Exception {
        FloorShortDTO floorShortDTO = new FloorShortDTO();
        floorShortDTO.setId(1L);
        floorShortDTO.setNumber(1);
        floorShortDTO.setName("Первый этаж");
        floorShortDTO.setPoints(createPoints());

        FloorDTO mapDTO = new FloorDTO(floorShortDTO, List.of(), List.of(), List.of());

        when(floorService.createFloor(eq(1L), eq(1), any(FloorDTO.class))).thenReturn(mapDTO);

        mockMvc.perform(post("/api/v1/admin/buildings/1/floors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mapDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.floor.id").value(1))
                .andExpect(jsonPath("$.floor.name").value("Первый этаж"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteFloor_ok() throws Exception {
        doNothing().when(floorService).deleteFloor(1L, 1);

        mockMvc.perform(delete("/api/v1/admin/buildings/1/floors/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(floorService).deleteFloor(1L, 1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void resetDatabase_ok() throws Exception {
        doNothing().when(dbService).resetDatabase();

        mockMvc.perform(delete("/api/v1/admin/reset-db")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(dbService).resetDatabase();
    }

    @Test
    void updateFloorData_unauthorized() throws Exception {
        FloorDTO mapDTO = createMapDTO();

        mockMvc.perform(put("/api/v1/admin/buildings/1/floors/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mapDTO)))
                .andExpect(status().isUnauthorized());
    }
}
