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
import org.mininuniver.interactiveMap.dto.map.RoomDTO;
import org.mininuniver.interactiveMap.mapper.RoomMapper;
import org.mininuniver.interactiveMap.model.Room;
import org.mininuniver.interactiveMap.repository.RoomRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomService roomService;

    @Test
    void getRoomByBuildingIdAndName_ok() {
        Room room = new Room();
        room.setId(0L);
        room.setName("A101");

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(0L);
        roomDTO.setName("A101");

        when(roomRepository.findByFloor_Building_IdAndName(1L, "A101"))
                .thenReturn(Optional.of(room));
        when(roomMapper.toDto(room))
                .thenReturn(roomDTO);

        var result = roomService.getRoomByBuildingIdAndName(1L, "A101");

        assertThat(result).isSameAs(roomDTO);
        verify(roomRepository).findByFloor_Building_IdAndName(1L, "A101");
        verify(roomMapper).toDto(room);
    }

    @Test
    void getRoomByBuildingIdAndName_notFound() {
        when(roomRepository.findByFloor_Building_IdAndName(1L, "A101")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.getRoomByBuildingIdAndName(1L, "A101"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("A101");

        verify(roomRepository).findByFloor_Building_IdAndName(1L, "A101");
        verifyNoInteractions(roomMapper);
    }

    @Test
    void getRoomByFloorIdAndName_ok() {
        Room room = new Room();
        room.setId(0L);
        room.setName("A101");

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(0L);
        roomDTO.setName("A101");

        when(roomRepository.findByFloorIdAndName(2L, "A101"))
                .thenReturn(Optional.of(room));
        when(roomMapper.toDto(room))
                .thenReturn(roomDTO);

        var result = roomService.getRoomByFloorIdAndName(2L, "A101");

        assertThat(result).isSameAs(roomDTO);
        verify(roomRepository).findByFloorIdAndName(2L, "A101");
        verify(roomMapper).toDto(room);
    }

    @Test
    void getRoomByBuildingIdAndFloorNumberAndName_ok() {
        Room room = new Room();
        room.setId(0L);
        room.setName("A101");

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(0L);
        roomDTO.setName("A101");

        when(roomRepository.findByFloor_Building_IdAndFloor_NumberAndName(1L, 1, "A101"))
                .thenReturn(Optional.of(room));
        when(roomMapper.toDto(room))
                .thenReturn(roomDTO);

        var result = roomService.getRoomByBuildingIdAndFloorNumberAndName(1L, 1, "A101");

        assertThat(result).isSameAs(roomDTO);
        verify(roomRepository).findByFloor_Building_IdAndFloor_NumberAndName(1L, 1, "A101");
        verify(roomMapper).toDto(room);
    }

    @Test
    void getAllRoomsByBuildingId_ok() {
        var rooms = List.of(new Room(), new Room());
        var dtos = List.of(new RoomDTO(), new RoomDTO());

        when(roomRepository.findByFloor_Building_Id(1L)).thenReturn(rooms);
        when(roomMapper.toDto(rooms.get(0))).thenReturn(dtos.get(0));
        when(roomMapper.toDto(rooms.get(1))).thenReturn(dtos.get(1));

        var result = roomService.getAllRoomsByBuildingId(1L);

        assertThat(result).containsExactlyElementsOf(dtos);
        verify(roomRepository).findByFloor_Building_Id(1L);
    }

    @Test
    void getAllRoomsByBuildingIdAndFloorNumber_ok() {
        var rooms = List.of(new Room(), new Room());
        var dtos = List.of(new RoomDTO(), new RoomDTO());

        when(roomRepository.findByFloor_Building_IdAndFloor_Number(1L, 1)).thenReturn(rooms);
        when(roomMapper.toDto(rooms.get(0))).thenReturn(dtos.get(0));
        when(roomMapper.toDto(rooms.get(1))).thenReturn(dtos.get(1));

        var result = roomService.getAllRoomsByBuildingIdAndFloorNumber(1L, 1);

        assertThat(result).containsExactlyElementsOf(dtos);
        verify(roomRepository).findByFloor_Building_IdAndFloor_Number(1L, 1);
    }

    @Test
    void getRoomById_ok() {
        Room room = new Room();
        room.setId(1L);
        room.setName("A101");

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1L);
        roomDTO.setName("A101");

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));
        when(roomMapper.toDto(room))
                .thenReturn(roomDTO);

        var result = roomService.getRoomById(1L);

        assertThat(result).isSameAs(roomDTO);
        verify(roomRepository).findById(1L);
        verify(roomMapper).toDto(room);
    }

    @Test
    void getRoomById_notFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.getRoomById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("1");

        verify(roomRepository).findById(1L);
    }

    @Test
        void getAllRooms_ok() {
            var rooms = List.of(new Room(), new Room());
            var dtos = List.of(new RoomDTO(), new RoomDTO());

            when(roomRepository.findAll()).thenReturn(rooms);
            when(roomMapper.toDto(rooms.get(0))).thenReturn(dtos.get(0));
            when(roomMapper.toDto(rooms.get(1))).thenReturn(dtos.get(1));

            var result = roomService.getAllRooms();

            assertThat(result).containsExactlyElementsOf(dtos);
            verify(roomRepository).findAll();
            verify(roomMapper).toDto(rooms.get(0));
            verify(roomMapper).toDto(rooms.get(1));
        }
}
