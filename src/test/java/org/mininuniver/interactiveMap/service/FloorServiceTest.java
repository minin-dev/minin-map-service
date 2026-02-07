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

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mininuniver.interactiveMap.dto.map.*;
import org.mininuniver.interactiveMap.mapper.FloorMapper;
import org.mininuniver.interactiveMap.mapper.NodeMapper;
import org.mininuniver.interactiveMap.mapper.RoomMapper;
import org.mininuniver.interactiveMap.mapper.StairsMapper;
import org.mininuniver.interactiveMap.model.Floor;
import org.mininuniver.interactiveMap.model.GraphNode;
import org.mininuniver.interactiveMap.model.Room;
import org.mininuniver.interactiveMap.model.Stairs;
import org.mininuniver.interactiveMap.repository.FloorRepository;
import org.mininuniver.interactiveMap.repository.NodeRepository;
import org.mininuniver.interactiveMap.repository.RoomRepository;
import org.mininuniver.interactiveMap.repository.StairsRepository;
import org.mininuniver.interactiveMap.repository.BuildingRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FloorServiceTest {

    @Mock
    private FloorMapper floorMapper;
    @Mock
    private RoomMapper roomMapper;
    @Mock
    private NodeMapper nodeMapper;
    @Mock
    private StairsMapper stairsMapper;
    @Mock
    private FloorRepository floorRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private NodeRepository nodeRepository;
    @Mock
    private StairsRepository stairsRepository;
    @Mock
    private BuildingRepository buildingRepository;

    @InjectMocks
    private FloorService floorService;

    private Floor floor;
    private FloorDTO floorDTO;
    private FloorShortDTO floorShortDTO;

    @BeforeEach
    void setUp() {
        floor = new Floor();
        floor.setId(1L);
        floor.setNumber(1);
        floor.setName("Первый этаж");
        floor.setPoints(createPoints());

        floorShortDTO = new FloorShortDTO();
        floorShortDTO.setId(1L);
        floorShortDTO.setNumber(1);
        floorShortDTO.setName("Первый этаж");
        floorShortDTO.setPoints(createPoints());

        floorDTO = new FloorDTO(floorShortDTO, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
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
    void getAllFloors_ok() {
        Floor floor2 = new Floor();
        floor2.setId(2L);
        floor2.setNumber(2);
        floor2.setName("Второй этаж");

        List<Floor> floors = List.of(floor, floor2);

        when(floorRepository.findAll()).thenReturn(floors);
        when(floorMapper.toShortDto(floor)).thenReturn(floorShortDTO);

        FloorShortDTO floorShortDTO2 = new FloorShortDTO();
        floorShortDTO2.setId(2L);
        floorShortDTO2.setNumber(2);
        floorShortDTO2.setName("Второй этаж");
        when(floorMapper.toShortDto(floor2)).thenReturn(floorShortDTO2);

        List<FloorShortDTO> result = floorService.getAllFloors();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNumber()).isEqualTo(1);
        assertThat(result.get(1).getNumber()).isEqualTo(2);
        verify(floorRepository).findAll();
    }

    @Test
    void getMap_ok() {
        List<Room> rooms = List.of(new Room());
        List<Stairs> stairs = List.of(new Stairs());
        List<GraphNode> nodes = List.of(new GraphNode());
        RoomDTO roomDTO = new RoomDTO();
        StairsDTO stairsDTO = new StairsDTO();
        NodeDTO nodeDTO = new NodeDTO();

        when(floorRepository.findByBuildingIdAndNumber(1L, 1)).thenReturn(Optional.of(floor));
        when(floorMapper.toShortDto(floor)).thenReturn(floorShortDTO);
        when(roomRepository.findByFloorId(1L)).thenReturn(rooms);
        when(stairsRepository.findByFloorId(1L)).thenReturn(stairs);
        when(nodeRepository.findByFloorId(1L)).thenReturn(nodes);

        when(roomMapper.toDto(any(Room.class))).thenReturn(roomDTO);
        when(stairsMapper.toDto(any(Stairs.class))).thenReturn(stairsDTO);
        when(nodeMapper.toDto(any(GraphNode.class))).thenReturn(nodeDTO);

        FloorDTO result = floorService.getFloorDataByBuildingIdAndNumber(1L, 1);

        assertThat(result).isNotNull();
        assertThat(result.getFloor()).isEqualTo(floorDTO.getFloor());
        assertThat(result.getRooms()).hasSize(1);
        assertThat(result.getStairs()).hasSize(1);
        assertThat(result.getNodes()).hasSize(1);
    }

    @Test
    void getMapData_floorNotFound() {
        when(floorRepository.findByBuildingIdAndNumber(1L, 99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> floorService.getFloorDataByBuildingIdAndNumber(1L, 99))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Этаж не найден");
    }

    @Test
    void createFloor_ok() {
        NodeDTO nodeDTO = new NodeDTO();
        nodeDTO.setId(-1L);
        nodeDTO.setPos(Map.of("x", 10, "y", 20));

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setName("A101");
        roomDTO.setNodeId(-1L);
        roomDTO.setPoints(createPoints());

        StairsDTO stairsDTO = new StairsDTO();
        stairsDTO.setFloorId(1L);
        stairsDTO.setPoints(createPoints());

        FloorDTO inputDTO = new FloorDTO(floorShortDTO, List.of(roomDTO), List.of(stairsDTO), List.of(nodeDTO));

        when(floorRepository.existsByBuildingIdAndNumber(1L, 1)).thenReturn(false);
        when(floorRepository.save(any(Floor.class))).thenReturn(floor);
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(new org.mininuniver.interactiveMap.model.Building()));

        GraphNode savedNode = new GraphNode();
        savedNode.setId(1L);
        when(nodeRepository.save(any(GraphNode.class))).thenReturn(savedNode);
        when(roomRepository.save(any(Room.class))).thenReturn(new Room());
        when(stairsRepository.save(any(Stairs.class))).thenReturn(new Stairs());

        when(floorRepository.findByBuildingIdAndNumber(1L, 1)).thenReturn(Optional.of(floor));
        when(floorMapper.toShortDto(floor)).thenReturn(floorShortDTO);
        when(roomRepository.findByFloorId(1L)).thenReturn(Collections.emptyList());
        when(stairsRepository.findByFloorId(1L)).thenReturn(Collections.emptyList());
        when(nodeRepository.findByFloorId(1L)).thenReturn(Collections.emptyList());

        FloorDTO result = floorService.createFloor(1L, 1, inputDTO);

        assertThat(result).isNotNull();
        verify(floorRepository).save(any(Floor.class));
        verify(nodeRepository).save(any(GraphNode.class));
    }

    @Test
    void createFloor_duplicateFloor() {
        when(floorRepository.existsByBuildingIdAndNumber(1L, 1)).thenReturn(true);

        assertThatThrownBy(() -> floorService.createFloor(1L, 1, new FloorDTO(floorShortDTO, List.of(), List.of(), List.of())))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessageContaining("уже существует");
    }

    @Test
    void updateFloorData_ok() {
        NodeDTO nodeDTO = new NodeDTO();
        nodeDTO.setId(1L);
        nodeDTO.setPos(Map.of("x", 10, "y", 20));
        nodeDTO.setNeighbors(new Long[]{});

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1L);
        roomDTO.setName("A101");
        roomDTO.setPoints(createPoints());

        StairsDTO stairsDTO = new StairsDTO();
        stairsDTO.setId(1L);
        stairsDTO.setFloorId(1L);
        stairsDTO.setPoints(createPoints());

        FloorDTO inputDTO = new FloorDTO(floorShortDTO, List.of(roomDTO), List.of(stairsDTO), List.of(nodeDTO));

        GraphNode existingNode = new GraphNode();
        existingNode.setId(1L);

        Room existingRoom = new Room();
        existingRoom.setId(1L);
        existingRoom.setName("A101");

        Stairs existingStairs = new Stairs();
        existingStairs.setId(1L);
        existingStairs.setFloor(this.floor);

        when(floorRepository.findByBuildingIdAndNumber(1L, 1)).thenReturn(Optional.of(floor));
        when(floorRepository.save(any(Floor.class))).thenReturn(floor);
        when(nodeRepository.findByFloorId(1L)).thenReturn(List.of(existingNode));
        when(roomRepository.findByFloorId(1L)).thenReturn(List.of(existingRoom));
        when(stairsRepository.findByFloorId(1L)).thenReturn(List.of(existingStairs));
        when(nodeRepository.save(any(GraphNode.class))).thenReturn(existingNode);
        when(roomRepository.save(any(Room.class))).thenReturn(existingRoom);
        when(stairsRepository.save(any(Stairs.class))).thenReturn(existingStairs);

        when(floorMapper.toShortDto(floor)).thenReturn(floorShortDTO);
        when(roomMapper.toDto(any(Room.class))).thenReturn(roomDTO);
        when(stairsMapper.toDto(any(Stairs.class))).thenReturn(stairsDTO);
        when(nodeMapper.toDto(any(GraphNode.class))).thenReturn(nodeDTO);

        FloorDTO result = floorService.updateFloorData(1L, 1, inputDTO);

        assertThat(result).isNotNull();
        verify(floorRepository, times(2)).findByBuildingIdAndNumber(1L, 1);
        verify(floorRepository).save(any(Floor.class));
    }

    @Test
    void updateFloorData_floorNotFound() {
        when(floorRepository.findByBuildingIdAndNumber(1L, 99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> floorService.updateFloorData(1L, 99, new FloorDTO(floorShortDTO, List.of(), List.of(), List.of())))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void deleteFloor_ok() {
        when(floorRepository.findByBuildingIdAndNumber(1L, 1)).thenReturn(Optional.of(floor));

        floorService.deleteFloor(1L, 1);

        verify(stairsRepository).deleteAllByFloorId(1L);
        verify(roomRepository).deleteAllByFloorId(1L);
        verify(nodeRepository).deleteAllByFloorId(1L);
        verify(floorRepository).delete(floor);
    }

    @Test
    void deleteFloor_notFound() {
        when(floorRepository.findByBuildingIdAndNumber(1L, 99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> floorService.deleteFloor(1L, 99))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void resetDatabase_ok() {
        floorService.resetDatabase();

        verify(roomRepository).deleteAll();
        verify(stairsRepository).deleteAll();
        verify(nodeRepository).deleteAll();
        verify(floorRepository).deleteAll();
        verify(floorRepository).resetSequences();
    }

    @Test
    void resetDatabase_error() {
        doThrow(new RuntimeException("DB error")).when(roomRepository).deleteAll();

        assertThatThrownBy(() -> floorService.resetDatabase())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ошибка при сбросе базы данных");
    }
}
