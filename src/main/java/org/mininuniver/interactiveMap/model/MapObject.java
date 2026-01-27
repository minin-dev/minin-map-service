package org.mininuniver.interactiveMap.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class MapObject extends DefaultObject {
    private String name;
}
