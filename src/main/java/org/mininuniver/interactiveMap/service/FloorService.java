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

package org.mininuniver.interactiveMap.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mininuniver.interactiveMap.dto.map.FloorDTO;
import org.mininuniver.interactiveMap.model.*;
import org.mininuniver.interactiveMap.repository.FloorRepository;
import org.mininuniver.interactiveMap.repository.BuildingRepository;
import org.mininuniver.interactiveMap.dto.map.NodeDTO;
import org.mininuniver.interactiveMap.dto.map.RoomDTO;
import org.mininuniver.interactiveMap.dto.map.StairsDTO;
import org.mininuniver.interactiveMap.dto.map.FloorShortDTO;
import org.mininuniver.interactiveMap.mapper.FloorMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@RequiredArgsConstructor
@Validated
public class FloorService {

    private final FloorMapper floorMapper;

    private final NodeService nodeService;
    private final RoomService roomService;
    private final StairsService stairsService;

    private final FloorRepository floorRepository;
    private final BuildingRepository buildingRepository;

    public List<FloorShortDTO> getAllFloors() {
        List<Floor> floors = floorRepository.findAll();
        return floors.stream()
                .sorted(Comparator.comparing(Floor::getNumber))
                .map(floorMapper::toShortDto)
                .toList();
    }

    public List<FloorShortDTO> searchFloors(Long buildingId) {
        if (buildingId != null) {
            return getFloorsByBuildingId(buildingId);
        }
        return getAllFloors();
    }

    public List<FloorShortDTO> getFloorsByBuildingId(Long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new EntityNotFoundException("Здание с id " + buildingId + " не найдено"));

        return building.getFloors().stream()
                .sorted(Comparator.comparing(Floor::getNumber))
                .map(floorMapper::toShortDto)
                .toList();
    }

    public FloorDTO getFloorById(Long id) {
        Floor floorEntity = floorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Здание с id " + id + " не найдено"));
        FloorShortDTO floor = floorMapper.toShortDto(floorEntity);

        List<RoomDTO> rooms = roomService.getRoomsByFloorId(floor.getId());
        List<StairsDTO> stairs = stairsService.getStairsByFloorId(floor.getId());
        List<NodeDTO> nodes = nodeService.getNodesByFloorId(floor.getId());

        return new FloorDTO(floor, rooms, stairs, nodes);
    }

    public FloorDTO getFloorDataByBuildingIdAndNumber(Long buildingId, int number) {
        Floor floorEntity = floorRepository.findByBuildingIdAndNumber(buildingId, number)
                .orElseThrow(() -> new EntityNotFoundException("Этаж не найден"));
        FloorShortDTO floor = floorMapper.toShortDto(floorEntity);

        List<RoomDTO> rooms = roomService.getRoomsByFloorId(floor.getId());
        List<StairsDTO> stairs = stairsService.getStairsByFloorId(floor.getId());
        List<NodeDTO> nodes = nodeService.getNodesByFloorId(floor.getId());

        return new FloorDTO(floor, rooms, stairs, nodes);
    }

    @Transactional
    public FloorDTO updateFloorData(Long buildingId, int number, @Valid FloorDTO mapDTO) {
        Floor floor = floorRepository.findByBuildingIdAndNumber(buildingId, number)
                .orElseThrow(() -> new EntityNotFoundException("Этаж с номером " + number + " в здании " + buildingId + " не найден"));
        floor.setNumber(number);
        floor.setName(mapDTO.getFloor().getName());
        floor.setPoints(mapDTO.getFloor().getPoints());
        floor = floorRepository.save(floor);

        Map<Long, Long> nodeIdMapping = nodeService.updateNodesForFloor(floor, mapDTO.getNodes());
        roomService.updateRoomsForFloor(floor, mapDTO.getRooms(), nodeIdMapping);
        stairsService.updateStairsForFloor(floor, mapDTO.getStairs(), nodeIdMapping);

        return getFloorDataByBuildingIdAndNumber(buildingId, number);
    }

    @Transactional
    public FloorDTO createFloor(Long buildingId, int number, @Valid FloorDTO mapDTO) {
        if (floorRepository.existsByBuildingIdAndNumber(buildingId, number))
            throw new DuplicateKeyException("Этаж с номером " + number + " уже существует в этом здании");

        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new EntityNotFoundException("Здание с id " + buildingId + " не найдено"));

        Floor floor = new Floor();
        floor.setBuilding(building);
        floor.setNumber(number);
        floor.setName(mapDTO.getFloor().getName());
        floor.setPoints(mapDTO.getFloor().getPoints());
        floor = floorRepository.save(floor);

        Map<Long, Long> nodeIdMapping = nodeService.createNodesForFloor(floor, mapDTO.getNodes());
        roomService.createRoomsForFloor(floor, mapDTO.getRooms(), nodeIdMapping);
        stairsService.createStairsForFloor(floor, mapDTO.getStairs(), nodeIdMapping);

        return getFloorDataByBuildingIdAndNumber(buildingId, number);
    }

    @Transactional
    public void deleteFloor(Long buildingId, int number) {
        Floor floor = floorRepository.findByBuildingIdAndNumber(buildingId, number)
                .orElseThrow(() -> new EntityNotFoundException("Этаж с номером " + number + " в здании " + buildingId + " не найден"));

        try {
            stairsService.deleteAllByFloorId(floor.getId());
            roomService.deleteAllByFloorId(floor.getId());
            nodeService.deleteAllByFloorId(floor.getId());
            floorRepository.delete(floor);
        } catch (OptimisticLockException e) {
            throw new RuntimeException("Ошибка при удалении этажа: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteAll() {
        floorRepository.deleteAll();
        floorRepository.resetSequences();
    }

}
