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
import org.mininuniver.interactiveMap.map.controller.AdminController;
import org.mininuniver.interactiveMap.map.dto.FloorDTO;
import org.mininuniver.interactiveMap.map.dto.FloorShortDTO;
import org.mininuniver.interactiveMap.map.dto.PointDTO;
import org.mininuniver.interactiveMap.map.service.BuildingService;
import org.mininuniver.interactiveMap.map.service.DBService;
import org.mininuniver.interactiveMap.map.service.FloorService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    private FloorService floorService;

    @Mock
    private BuildingService buildingService;

    @Mock
    private DBService dbService;

    @InjectMocks
    private AdminController adminController;

    private FloorDTO floorDTO;
    private FloorShortDTO floorShortDTO;

    @BeforeEach
    void setUp() {
        floorShortDTO = new FloorShortDTO();
        floorShortDTO.setId(1L);
        floorShortDTO.setNumber(1);
        floorShortDTO.setName("Первый этаж");
        floorShortDTO.setPoints(createPoints());

        floorDTO = new FloorDTO(floorShortDTO, List.of(), List.of(), List.of());
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
    void updateFloorData_ok() {
        when(floorService.updateFloorData(1L, 1, floorDTO)).thenReturn(floorDTO);

        FloorDTO result = adminController.updateFloorData(1L, 1, floorDTO);

        assertThat(result).isEqualTo(floorDTO);
        verify(floorService).updateFloorData(1L, 1, floorDTO);
    }

    @Test
    void createFloor_ok() {
        when(floorService.createFloor(1L, 1, floorDTO)).thenReturn(floorDTO);

        FloorDTO result = adminController.createFloor(1L, 1, floorDTO);

        assertThat(result).isEqualTo(floorDTO);
        verify(floorService).createFloor(1L, 1, floorDTO);
    }

    @Test
    void deleteFloor_ok() {
        var response = adminController.deleteFloor(1L, 1);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        verify(floorService).deleteFloor(1L, 1);
    }

    @Test
    void resetDatabase_ok() {
        var response = adminController.resetDatabase();

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        verify(dbService).resetDatabase();
    }
}
