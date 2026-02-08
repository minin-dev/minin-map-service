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

package org.mininuniver.interactiveMap.repository;

import org.mininuniver.interactiveMap.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Room findByFloorIdAndName(Long buildingId, String name);
    Optional<Room> findByName(String name);
    List<Room> findByFloorId(Long floorId);
    List<Room> findAllByFloorId(Long floorId);
    List<Room> findByFloor_Building_Id(Long buildingId);
    List<Room> findByFloor_Building_IdAndFloor_Number(Long buildingId, int floorNumber);
    void deleteAllByFloorId(Long floorId);
}