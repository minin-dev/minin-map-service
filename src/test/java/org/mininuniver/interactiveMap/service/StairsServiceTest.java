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
import org.mininuniver.interactiveMap.dto.map.PointDTO;
import org.mininuniver.interactiveMap.dto.map.StairsDTO;
import org.mininuniver.interactiveMap.mapper.StairsMapper;
import org.mininuniver.interactiveMap.model.Floor;
import org.mininuniver.interactiveMap.model.GraphNode;
import org.mininuniver.interactiveMap.model.Stairs;
import org.mininuniver.interactiveMap.repository.StairsRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StairsServiceTest {

    @Captor
    private ArgumentCaptor<Iterable<Stairs>> stairsCaptor;

    @Mock
    private StairsRepository stairsRepository;

    @Mock
    private StairsMapper stairsMapper;

    @InjectMocks
    private StairsService stairsService;

    @Test
    void getStairsByFloorId_shouldReturnListOfStairsDTO() {
        Long floorId = 1L;
        Stairs stairs = new Stairs();
        StairsDTO stairsDTO = new StairsDTO();

        when(stairsRepository.findByFloorId(floorId)).thenReturn(List.of(stairs));
        when(stairsMapper.toDto(stairs)).thenReturn(stairsDTO);

        List<StairsDTO> result = stairsService.getStairsByFloorId(floorId);

        assertThat(result).containsExactly(stairsDTO);
        verify(stairsRepository).findByFloorId(floorId);
        verify(stairsMapper).toDto(stairs);
    }

    @Test
    void deleteAllByFloorId_shouldCallRepositoryDelete() {
        Long floorId = 1L;
        stairsService.deleteAllByFloorId(floorId);
        verify(stairsRepository).deleteAllByFloorId(floorId);
    }

    @Test
    void deleteAll_shouldCallRepositoryDelete() {
        stairsService.deleteAll();
        verify(stairsRepository).deleteAll();
    }

    @Test
    void createStairsForFloor_shouldSaveStairs() {
        Floor floor = new Floor();
        floor.setId(1L);
        StairsDTO stairsDTO = new StairsDTO();

        PointDTO p1 = new PointDTO(); p1.setX(0); p1.setY(0);
        PointDTO p2 = new PointDTO(); p2.setX(10); p2.setY(0);
        PointDTO p3 = new PointDTO(); p3.setX(10); p3.setY(10);
        List<PointDTO> points = List.of(p1, p2, p3);
        Long[] stairsArray = new Long[]{1L, 2L};

        stairsDTO.setPoints(points);
        stairsDTO.setStairs(stairsArray);
        stairsDTO.setNodeId(100L);
        Map<Long, Long> nodeIdMapping = Map.of(100L, 200L);

        stairsService.createStairsForFloor(floor, List.of(stairsDTO), nodeIdMapping);

        verify(stairsRepository).save(argThat(stairs ->
            stairs.getFloor().equals(floor) &&
            stairs.getPoints().equals(points) &&
            Arrays.equals(stairs.getStairs(), stairsArray) &&
            stairs.getNode().getId().equals(200L)
        ));
    }

    @Test
    void updateStairsForFloor_shouldUpdateExistingAndCreateNew() {
        Floor floor = new Floor();
        floor.setId(1L);

        Stairs existingStairs = new Stairs();
        existingStairs.setId(10L);
        existingStairs.setFloor(floor);

        StairsDTO updateDTO = new StairsDTO();
        updateDTO.setId(10L);

        PointDTO p1 = new PointDTO(); p1.setX(0); p1.setY(0);
        List<PointDTO> newPoints = List.of(p1);
        updateDTO.setPoints(newPoints);

        StairsDTO newDTO = new StairsDTO();
        PointDTO p2 = new PointDTO(); p2.setX(10); p2.setY(10);
        List<PointDTO> newPoints2 = List.of(p2);
        newDTO.setPoints(newPoints2);

        Map<Long, Long> nodeIdMapping = Map.of();

        when(stairsRepository.findByFloorId(floor.getId())).thenReturn(List.of(existingStairs));

        stairsService.updateStairsForFloor(floor, List.of(updateDTO, newDTO), nodeIdMapping);

        // existingStairs should be updated
        assertThat(existingStairs.getPoints()).isEqualTo(newPoints);

        // should save existingStairs and new Stairs
        verify(stairsRepository, times(2)).save(any(Stairs.class));

        // should delete nothing since existingStairs was used
        verify(stairsRepository).deleteAll(stairsCaptor.capture());
        assertThat(stairsCaptor.getValue()).isEmpty();
    }

    @Test
    void updateStairsForFloor_shouldDeleteRemovedStairs() {
        Floor floor = new Floor();
        floor.setId(1L);

        Stairs existingStairs = new Stairs();
        existingStairs.setId(10L);

        when(stairsRepository.findByFloorId(floor.getId())).thenReturn(List.of(existingStairs));

        // No DTOs provided, so existing should be deleted
        stairsService.updateStairsForFloor(floor, Collections.emptyList(), Map.of());

        verify(stairsRepository).deleteAll(stairsCaptor.capture());
        assertThat(stairsCaptor.getValue()).containsExactly(existingStairs);
        verify(stairsRepository, never()).save(any());
    }
}
