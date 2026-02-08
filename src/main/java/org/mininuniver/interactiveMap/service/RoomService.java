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
import lombok.RequiredArgsConstructor;
import org.mininuniver.interactiveMap.dto.map.RoomDTO;
import org.mininuniver.interactiveMap.repository.RoomRepository;
import org.mininuniver.interactiveMap.mapper.RoomMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomMapper roomMapper;
    private final RoomRepository roomRepository;

    public RoomDTO getRoomByFloorIdAndName(Long floorId, String name) {
        return Optional.ofNullable(roomRepository.findByFloorIdAndName(floorId, name))
                .map(roomMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Комната с именем '" + name + "' на этаже с ID " + floorId + " не найдена"));
    }

    public RoomDTO getRoomByName(String name) {
        return roomRepository.findByName(name)
                .map(roomMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Комната с именем '" + name + "' не найдена"));
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
}
