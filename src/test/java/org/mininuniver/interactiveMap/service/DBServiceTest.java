package org.mininuniver.interactiveMap.service;

import org.mininuniver.interactiveMap.map.service.BuildingService;
import org.mininuniver.interactiveMap.map.service.DBService;
import org.mininuniver.interactiveMap.map.service.FloorService;
import org.mininuniver.interactiveMap.map.service.NodeService;
import org.mininuniver.interactiveMap.map.service.RoomService;
import org.mininuniver.interactiveMap.map.service.StairsService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DBServiceTest {

    @Mock
    private RoomService roomService;
    @Mock
    private StairsService stairsService;
    @Mock
    private NodeService nodeService;
    @InjectMocks
    private DBService dbService;
    @Mock
    private BuildingService buildingService;
    @Mock
    private FloorService floorService;

    @Test
    void resetDatabase_ok() {
        dbService.resetDatabase();

        verify(roomService).deleteAll();
        verify(stairsService).deleteAll();
        verify(nodeService).deleteAll();
        verify(floorService).deleteAll();
        verify(buildingService).deleteAll();
    }

    @Test
    void resetDatabase_error() {
        doThrow(new RuntimeException("DB error")).when(roomService).deleteAll();

        assertThatThrownBy(() -> dbService.resetDatabase())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ошибка при сбросе базы данных");
    }
}
