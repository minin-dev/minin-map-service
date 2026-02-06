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

import org.mininuniver.interactiveMap.model.Floor;
import org.mininuniver.interactiveMap.model.Stairs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StairsRepository extends JpaRepository<Stairs, Long>{
    default List<Stairs> findByFloorId(Long floorId) {
        return findAll().stream()
                .filter(stairs -> {
                    Long[] floors = stairs.getFloors();
                    if (floors != null) {
                        for (Long fId : floors) {
                            if (fId.equals(floorId)) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .toList();
    };

    default void removeFloorByFloorId(Long floorId) {
        List<Stairs> stairsToDelete = findByFloorId(floorId);
        for (Stairs stairs : stairsToDelete) {
            Long[] floors = stairs.getFloors();
            if (floors != null) {
                Long[] newFloors = new Long[floors.length - 1];
                int index = 0;
                for (Long fId : floors) {
                    if (!fId.equals(floorId)) {
                        newFloors[index++] = fId;
                    }
                }
                if (floors.length == 1) delete(stairs);
                stairs.setFloors(newFloors);
                save(stairs);
            }
        }
    }
}
