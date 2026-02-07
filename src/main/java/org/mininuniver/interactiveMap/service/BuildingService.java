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
