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

package org.mininuniver.interactiveMap.map.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mininuniver.interactiveMap.map.dto.FloorDTO;
import org.mininuniver.interactiveMap.map.model.*;
import org.mininuniver.interactiveMap.map.repository.FloorRepository;
import org.mininuniver.interactiveMap.map.repository.BuildingRepository;
import org.mininuniver.interactiveMap.map.dto.NodeDTO;
import org.mininuniver.interactiveMap.map.dto.RoomDTO;
import org.mininuniver.interactiveMap.map.dto.StairsDTO;
import org.mininuniver.interactiveMap.map.dto.FloorShortDTO;
import org.mininuniver.interactiveMap.map.mapper.FloorMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

/**
 * The type Floor service.
 */
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

    /**
     * Gets all floors.
     *
     * @return the all floors
     */
    public List<FloorShortDTO> getAllFloors() {
        List<Floor> floors = floorRepository.findAll();
        return floors.stream()
                .sorted(Comparator.comparing(Floor::getNumber))
                .map(floorMapper::toShortDto)
                .toList();
    }

    /**
     * Search floors list.
     *
     * @param buildingId the building id
     * @return the list
     */
    public List<FloorShortDTO> searchFloors(Long buildingId) {
        if (buildingId != null) {
            return getFloorsByBuildingId(buildingId);
        }
        return getAllFloors();
    }

    /**
     * Gets floors by building id.
     *
     * @param buildingId the building id
     * @return the floors by building id
     */
    public List<FloorShortDTO> getFloorsByBuildingId(Long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new EntityNotFoundException("Здание с id " + buildingId + " не найдено"));

        return building.getFloors().stream()
                .sorted(Comparator.comparing(Floor::getNumber))
                .map(floorMapper::toShortDto)
                .toList();
    }

    /**
     * Gets floor by id.
     *
     * @param id the id
     * @return the floor by id
     */
    public FloorDTO getFloorById(Long id) {
        Floor floorEntity = floorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Здание с id " + id + " не найдено"));
        FloorShortDTO floor = floorMapper.toShortDto(floorEntity);

        List<RoomDTO> rooms = roomService.getRoomsByFloorId(floor.getId());
        List<StairsDTO> stairs = stairsService.getStairsByFloorId(floor.getId());
        List<NodeDTO> nodes = nodeService.getNodesByFloorId(floor.getId());

        return new FloorDTO(floor, rooms, stairs, nodes);
    }

    /**
     * Gets floor data by building id and number.
     *
     * @param buildingId the building id
     * @param number     the number
     * @return the floor data by building id and number
     */
    public FloorDTO getFloorDataByBuildingIdAndNumber(Long buildingId, int number) {
        Floor floorEntity = floorRepository.findByBuildingIdAndNumber(buildingId, number)
                .orElseThrow(() -> new EntityNotFoundException("Этаж не найден"));
        FloorShortDTO floor = floorMapper.toShortDto(floorEntity);

        List<RoomDTO> rooms = roomService.getRoomsByFloorId(floor.getId());
        List<StairsDTO> stairs = stairsService.getStairsByFloorId(floor.getId());
        List<NodeDTO> nodes = nodeService.getNodesByFloorId(floor.getId());

        return new FloorDTO(floor, rooms, stairs, nodes);
    }

    /**
     * Update floor data floor dto.
     *
     * @param buildingId the building id
     * @param number     the number
     * @param mapDTO     the map dto
     * @return the floor dto
     */
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

    /**
     * Create floor floor dto.
     *
     * @param buildingId the building id
     * @param number     the number
     * @param mapDTO     the map dto
     * @return the floor dto
     */
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

    /**
     * Delete floor.
     *
     * @param buildingId the building id
     * @param number     the number
     */
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

    /**
     * Delete all.
     */
    @Transactional
    public void deleteAll() {
        floorRepository.deleteAll();
        floorRepository.resetSequences();
    }

}
