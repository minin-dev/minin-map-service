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

package org.mininuniver.interactiveMap.map.repository;

import org.mininuniver.interactiveMap.map.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The interface Room repository.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    /**
     * Find by floor building id and name optional.
     *
     * @param buildingId the building id
     * @param name       the name
     * @return the optional
     */
    Optional<Room> findByFloor_Building_IdAndName(Long buildingId, String name);

    /**
     * Find by floor id and name optional.
     *
     * @param floorId the floor id
     * @param name    the name
     * @return the optional
     */
    Optional<Room> findByFloorIdAndName(Long floorId, String name);

    /**
     * Find by floor building id and floor number and name optional.
     *
     * @param buildingId  the building id
     * @param floorNumber the floor number
     * @param name        the name
     * @return the optional
     */
    Optional<Room> findByFloor_Building_IdAndFloor_NumberAndName(Long buildingId, int floorNumber, String name);

    /**
     * Find by name optional.
     *
     * @param name the name
     * @return the optional
     */
    Optional<Room> findByName(String name);

    /**
     * Find by floor id list.
     *
     * @param floorId the floor id
     * @return the list
     */
    List<Room> findByFloorId(Long floorId);

    /**
     * Find all by floor id list.
     *
     * @param floorId the floor id
     * @return the list
     */
    List<Room> findAllByFloorId(Long floorId);

    /**
     * Find by floor building id list.
     *
     * @param buildingId the building id
     * @return the list
     */
    List<Room> findByFloor_Building_Id(Long buildingId);

    /**
     * Find by floor building id and floor number list.
     *
     * @param buildingId  the building id
     * @param floorNumber the floor number
     * @return the list
     */
    List<Room> findByFloor_Building_IdAndFloor_Number(Long buildingId, int floorNumber);

    /**
     * Delete all by floor id.
     *
     * @param floorId the floor id
     */
    void deleteAllByFloorId(Long floorId);
}