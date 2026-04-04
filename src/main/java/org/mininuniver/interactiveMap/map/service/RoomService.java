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
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;
import org.mininuniver.interactiveMap.map.model.Floor;
import org.mininuniver.interactiveMap.map.model.GraphNode;
import org.mininuniver.interactiveMap.map.model.Room;
import org.mininuniver.interactiveMap.map.repository.NodeRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import org.mininuniver.interactiveMap.map.dto.RoomDTO;
import org.mininuniver.interactiveMap.map.repository.RoomRepository;
import org.mininuniver.interactiveMap.map.mapper.RoomMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomMapper roomMapper;
    private final RoomRepository roomRepository;
    private final NodeRepository nodeRepository;

    @Transactional
    public void deleteAllByFloorId(Long floorId) {
        roomRepository.deleteAllByFloorId(floorId);
    }

    @Transactional
    public void deleteAll() {
        roomRepository.deleteAll();
    }

    @Transactional
    public void updateRoomsForFloor(Floor floor, List<RoomDTO> roomDTOs, Map<Long, Long> nodeIdMapping) {
        List<Room> existingRooms = roomRepository.findByFloorId(floor.getId());
        Map<Long, Room> existingRoomsMap = existingRooms.stream()
                .collect(Collectors.toMap(Room::getId, r -> r));

        Map<String, Room> roomsByName = existingRooms.stream()
                .collect(Collectors.toMap(Room::getName, r -> r, (r1, r2) -> r1));

        for (RoomDTO roomDTO : roomDTOs) {
            Room room;
            if (roomDTO.getId() != null && existingRoomsMap.containsKey(roomDTO.getId())) {
                room = existingRoomsMap.get(roomDTO.getId());
                existingRoomsMap.remove(roomDTO.getId());
            } else if (roomDTO.getName() != null && roomsByName.containsKey(roomDTO.getName())) {
                room = roomsByName.get(roomDTO.getName());
                existingRoomsMap.remove(room.getId());
            } else {
                room = new Room();
            }

            room.setName(roomDTO.getName());
            room.setFloor(floor);
            room.setPoints(roomDTO.getPoints());

            if (roomDTO.getNodeId() != null) {
                Long mappedNodeId = nodeIdMapping.getOrDefault(roomDTO.getNodeId(), roomDTO.getNodeId());
                GraphNode node = nodeRepository.findById(mappedNodeId)
                        .orElseThrow(() -> new EntityNotFoundException("GraphNode с id " + mappedNodeId + " не найден"));
                room.setNode(node);
            }

            roomRepository.save(room);
        }

        roomRepository.deleteAll(existingRoomsMap.values());
    }

    @Transactional
    public void createRoomsForFloor(Floor floor, List<RoomDTO> roomDTOs, Map<Long, Long> nodeIdMapping) {
        for (RoomDTO roomDTO : roomDTOs) {
            Room room = new Room();
            room.setName(roomDTO.getName());
            room.setFloor(floor);
            room.setPoints(roomDTO.getPoints());

            if (roomDTO.getNodeId() != null) {
                Long mappedNodeId = nodeIdMapping.getOrDefault(roomDTO.getNodeId(), roomDTO.getNodeId());
                GraphNode node = nodeRepository.findById(mappedNodeId)
                        .orElseThrow(() -> new EntityNotFoundException("GraphNode с id " + mappedNodeId + " не найден"));
                room.setNode(node);
            }

            roomRepository.save(room);
        }
    }

    public List<RoomDTO> getRoomsByFloorId(Long floorId) {
        return roomRepository.findByFloorId(floorId).stream()
                .map(roomMapper::toDto)
                .toList();
    }

    public RoomDTO getRoomByFloorIdAndName(Long floorId, String name) {
        return roomRepository.findByFloorIdAndName(floorId, name)
                .map(roomMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Комната с именем '" + name + "' на этаже с ID " + floorId + " не найдена"));
    }

    public RoomDTO getRoomByBuildingIdAndName(Long buildingId, String name) {
        return roomRepository.findByFloor_Building_IdAndName(buildingId, name)
                .map(roomMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Комната с именем '" + name + "' не найдена в здании"));
    }

    public RoomDTO getRoomByBuildingIdAndFloorNumberAndName(Long buildingId, int floorNumber, String name) {
        return roomRepository.findByFloor_Building_IdAndFloor_NumberAndName(buildingId, floorNumber, name)
                .map(roomMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Комната с именем '" + name + "' не найдена на этаже " + floorNumber + " в здании " + buildingId));
    }

    public RoomDTO getRoomById(Long id) {
        return roomRepository.findById(id)
                .map(roomMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Комната с id " + id + " не найдена"));
    }

    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(roomMapper::toDto)
                .toList();
    }

    public List<RoomDTO> getAllRoomsByBuildingId(Long buildingId) {
        return roomRepository.findByFloor_Building_Id(buildingId).stream()
                .map(roomMapper::toDto)
                .toList();
    }

    public List<RoomDTO> getAllRoomsByBuildingIdAndFloorNumber(Long buildingId, int floorNumber) {
        return roomRepository.findByFloor_Building_IdAndFloor_Number(buildingId, floorNumber).stream()
                .map(roomMapper::toDto)
                .toList();
    }

    public List<RoomDTO> searchRooms(Long buildingId, Integer floor, String name) {
        if (buildingId != null && floor != null && name != null) {
            return roomRepository.findByFloor_Building_IdAndFloor_NumberAndName(buildingId, floor, name)
                    .map(roomMapper::toDto)
                    .map(List::of)
                    .orElse(List.of());
        }
        if (buildingId != null && floor != null) {
            return getAllRoomsByBuildingIdAndFloorNumber(buildingId, floor);
        }
        if (buildingId != null && name != null) {
            return roomRepository.findByFloor_Building_IdAndName(buildingId, name)
                    .map(roomMapper::toDto)
                    .map(List::of)
                    .orElse(List.of());
        }
        if (buildingId != null) {
            return getAllRoomsByBuildingId(buildingId);
        }
        return getAllRooms();
    }
}
