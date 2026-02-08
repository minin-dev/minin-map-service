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
import org.mininuniver.interactiveMap.dto.map.BuildingDTO;
import org.mininuniver.interactiveMap.dto.map.BuildingShortDTO;
import org.mininuniver.interactiveMap.mapper.BuildingMapper;
import org.mininuniver.interactiveMap.model.Building;
import org.mininuniver.interactiveMap.repository.BuildingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingService {
    private final BuildingRepository buildingRepository;
    private final BuildingMapper buildingMapper;

    public List<BuildingShortDTO> getAllBuildings() {
        return buildingMapper.toShortDtoList(buildingRepository.findAll());
    }

    public BuildingDTO getBuildingById(Long id) {
        return buildingRepository.findById(id)
                .map(buildingMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Здание с id " + id + " не найдено"));
    }

    @Transactional
    public BuildingShortDTO createBuilding(BuildingShortDTO buildingDTO) {
        Building building = new Building();
        building.setName(buildingDTO.getName());
        building.setCoords(buildingDTO.getCoords());
        return buildingMapper.toShortDto(buildingRepository.save(building));
    }

    @Transactional
    public BuildingShortDTO updateBuilding(Long id, BuildingShortDTO buildingDTO) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Здание с id " + id + " не найдено"));
        building.setName(buildingDTO.getName());
        building.setCoords(buildingDTO.getCoords());
        return buildingMapper.toShortDto(buildingRepository.save(building));
    }

    @Transactional
    public void deleteBuilding(Long id) {
        if (!buildingRepository.existsById(id)) {
            throw new EntityNotFoundException("Здание с id " + id + " не найдено");
        }
        buildingRepository.deleteById(id);
    }
}
