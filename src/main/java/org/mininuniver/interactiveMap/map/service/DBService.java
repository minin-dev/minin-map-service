package org.mininuniver.interactiveMap.map.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The type Db service.
 */
@Service
@RequiredArgsConstructor
public class DBService {

    private final RoomService roomService;
    private final StairsService stairsService;
    private final NodeService nodeService;
    private final BuildingService buildingService;
    private final FloorService floorService;

    /**
     * Reset database.
     */
    @Transactional
    public void resetDatabase() {
        try {
            roomService.deleteAll();
            stairsService.deleteAll();
            nodeService.deleteAll();
            floorService.deleteAll();
            buildingService.deleteAll();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сбросе базы данных: " + e.getMessage());
        }
    }
}
