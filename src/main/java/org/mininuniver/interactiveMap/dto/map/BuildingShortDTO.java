package org.mininuniver.interactiveMap.dto.map;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BuildingShortDTO {
    private Long id;

    @NotBlank(message = "Имя здания не может быть пустым")
    private String name;

    private String coords;
}
