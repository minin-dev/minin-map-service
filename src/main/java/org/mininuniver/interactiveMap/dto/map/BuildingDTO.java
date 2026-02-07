package org.mininuniver.interactiveMap.dto.map;

import lombok.Data;

import java.util.List;

@Data
public class BuildingDTO {
    private final BuildingShortDTO building;
    private final List<FloorDTO> floors;
}
