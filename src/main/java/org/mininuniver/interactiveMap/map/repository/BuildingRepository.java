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

import org.mininuniver.interactiveMap.map.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The interface Building repository.
 */
@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    Optional<Building> findById(Long id);

    /**
     * Reset sequences.
     */
    @Modifying
    @Query(value = "DO $$ DECLARE seq RECORD; " +
            "BEGIN " +
            "  FOR seq IN SELECT c.oid::regclass::text AS seqname " +
            "             FROM pg_class c " +
            "             JOIN pg_namespace n ON n.oid = c.relnamespace " +
            "             WHERE c.relkind = 'S' AND n.nspname = 'public' " +
            "  LOOP " +
            "    EXECUTE 'ALTER SEQUENCE ' || seq.seqname || ' RESTART WITH 1'; " +
            "  END LOOP; " +
            "END $$;", nativeQuery = true)
    void resetSequences();
}
