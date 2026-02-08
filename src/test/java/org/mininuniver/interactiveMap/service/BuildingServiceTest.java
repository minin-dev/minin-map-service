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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mininuniver.interactiveMap.dto.map.BuildingDTO;
import org.mininuniver.interactiveMap.dto.map.BuildingShortDTO;
import org.mininuniver.interactiveMap.mapper.BuildingMapper;
import org.mininuniver.interactiveMap.model.Building;
import org.mininuniver.interactiveMap.repository.BuildingRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuildingServiceTest {

    @Mock
    private BuildingRepository buildingRepository;

    @Mock
    private BuildingMapper buildingMapper;

    @InjectMocks
    private BuildingService buildingService;

    @Test
    void getAllBuildings_shouldReturnListOfBuildingShortDTO() {
        Building building = new Building();
        BuildingShortDTO buildingShortDTO = new BuildingShortDTO();

        when(buildingRepository.findAll()).thenReturn(List.of(building));
        when(buildingMapper.toShortDtoList(List.of(building))).thenReturn(List.of(buildingShortDTO));

        List<BuildingShortDTO> result = buildingService.getAllBuildings();

        assertThat(result).containsExactly(buildingShortDTO);
        verify(buildingRepository).findAll();
        verify(buildingMapper).toShortDtoList(List.of(building));
    }

    @Test
    void getBuildingById_shouldReturnBuildingDTO_whenBuildingExists() {
        Long id = 1L;
        Building building = new Building();
        BuildingDTO buildingDTO = new BuildingDTO(new BuildingShortDTO(), List.of());

        when(buildingRepository.findById(id)).thenReturn(Optional.of(building));
        when(buildingMapper.toDto(building)).thenReturn(buildingDTO);

        BuildingDTO result = buildingService.getBuildingById(id);

        assertThat(result).isEqualTo(buildingDTO);
        verify(buildingRepository).findById(id);
        verify(buildingMapper).toDto(building);
    }

    @Test
    void getBuildingById_shouldThrowEntityNotFoundException_whenBuildingDoesNotExist() {
        Long id = 1L;
        when(buildingRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buildingService.getBuildingById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Здание с id " + id + " не найдено");
        verify(buildingRepository).findById(id);
    }

    @Test
    void createBuilding_shouldSaveAndReturnBuildingShortDTO() {
        BuildingShortDTO inputDto = new BuildingShortDTO();
        inputDto.setName("Building A");
        inputDto.setCoords("[0,0]");

        Building savedBuilding = new Building();
        savedBuilding.setName("Building A");
        savedBuilding.setCoords("[0,0]");

        BuildingShortDTO outputDto = new BuildingShortDTO();
        outputDto.setName("Building A");

        when(buildingRepository.save(any(Building.class))).thenReturn(savedBuilding);
        when(buildingMapper.toShortDto(savedBuilding)).thenReturn(outputDto);

        BuildingShortDTO result = buildingService.createBuilding(inputDto);

        assertThat(result).isEqualTo(outputDto);
        verify(buildingRepository).save(any(Building.class));
        verify(buildingMapper).toShortDto(savedBuilding);
    }

    @Test
    void updateBuilding_shouldUpdateAndReturnBuildingShortDTO_whenBuildingExists() {
        Long id = 1L;
        BuildingShortDTO inputDto = new BuildingShortDTO();
        inputDto.setName("Updated Building");
        inputDto.setCoords("[1,1]");

        Building existingBuilding = new Building();
        existingBuilding.setId(id);
        existingBuilding.setName("Old Building");

        Building updatedBuilding = new Building();
        updatedBuilding.setId(id);
        updatedBuilding.setName("Updated Building");

        BuildingShortDTO outputDto = new BuildingShortDTO();
        outputDto.setName("Updated Building");

        when(buildingRepository.findById(id)).thenReturn(Optional.of(existingBuilding));
        when(buildingRepository.save(any(Building.class))).thenReturn(updatedBuilding);
        when(buildingMapper.toShortDto(updatedBuilding)).thenReturn(outputDto);

        BuildingShortDTO result = buildingService.updateBuilding(id, inputDto);

        assertThat(result).isEqualTo(outputDto);
        assertThat(existingBuilding.getName()).isEqualTo("Updated Building");
        assertThat(existingBuilding.getCoords()).isEqualTo("[1,1]");
        verify(buildingRepository).findById(id);
        verify(buildingRepository).save(existingBuilding);
    }

    @Test
    void updateBuilding_shouldThrowEntityNotFoundException_whenBuildingDoesNotExist() {
        Long id = 1L;
        BuildingShortDTO inputDto = new BuildingShortDTO();

        when(buildingRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buildingService.updateBuilding(id, inputDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Здание с id " + id + " не найдено");
        verify(buildingRepository).findById(id);
        verify(buildingRepository, never()).save(any());
    }

    @Test
    void deleteBuilding_shouldDeleteBuilding_whenBuildingExists() {
        Long id = 1L;
        when(buildingRepository.existsById(id)).thenReturn(true);

        buildingService.deleteBuilding(id);

        verify(buildingRepository).existsById(id);
        verify(buildingRepository).deleteById(id);
    }

    @Test
    void deleteBuilding_shouldThrowEntityNotFoundException_whenBuildingDoesNotExist() {
        Long id = 1L;
        when(buildingRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> buildingService.deleteBuilding(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Здание с id " + id + " не найдено");
        verify(buildingRepository).existsById(id);
        verify(buildingRepository, never()).deleteById(any());
    }
}
