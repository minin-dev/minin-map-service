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

package org.mininuniver.interactiveMap.model;

import io.hypersistence.utils.hibernate.type.array.LongArrayType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.Map;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "Nodes")
@Schema(name = "NodeEntity", description = "Модель узла (entity)")
public class Node {
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id")
    private Floor floor;

    @Column(columnDefinition = "jsonb")
    @Type(JsonBinaryType.class)
    private Map<String, Object> pos;

    @Column(name = "neighbors", columnDefinition = "bigint[]")
    @Type(LongArrayType.class)
    private Long[] neighbors;

}
