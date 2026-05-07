package org.mininuniver.interactiveMap.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mininuniver.interactiveMap.map.mapper.NodeMapper;
import org.mininuniver.interactiveMap.map.service.*;
import org.mininuniver.interactiveMap.map.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * The type Data seeder.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final BuildingService buildingService;
    private final FloorService floorService;
    private final NodeService nodeService;
    private final RoomService roomService;
    private final StairsService stairsService;

    @Value("${app.mode:prod}")
    private String appMode;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (appMode.equals("dev") && buildingService.getAllBuildings().isEmpty()) {
            for (int i : new int[]{1, 3, 4, 5, 7, 8}) {
                seedData(i);
            }
            log.info("All buildings have been seeded");
        }
    }

    private void seedData(int modificator) {
        BuildingShortDTO bDto = new BuildingShortDTO();
        bDto.setName("Корпус " + modificator);
        bDto.setCoords(((56.326 + modificator * 0.001) + ", " + (44.005 + modificator * 0.001)));
        bDto = buildingService.createBuilding(bDto);

        for (int floorNum = 1; floorNum <= 2; floorNum++) {
            FloorShortDTO fDto = new FloorShortDTO();
            fDto.setNumber(floorNum);
            fDto.setName("Этаж " + floorNum);
            fDto.setBuildingId(bDto.getId());
            fDto.setPoints(Arrays.asList(
                    new PointDTO(0, 0),
                    new PointDTO(1000, 0),
                    new PointDTO(1000, 500),
                    new PointDTO(0, 500)
            ));

            List<NodeDTO> nodes = new ArrayList<>();
            List<RoomDTO> rooms = new ArrayList<>();
            List<StairsDTO> stairs = new ArrayList<>();

            NodeDTO node1 = new NodeDTO();
            node1.setId(-1L);
            node1.setPos(Map.of("x", 200, "y", 250));

            NodeDTO node2 = new NodeDTO();
            node2.setId(-2L);
            node2.setPos(Map.of("x", 500, "y", 250));

            NodeDTO node3 = new NodeDTO();
            node3.setId(-3L);
            node3.setPos(Map.of("x", 800, "y", 250));

            node1.setNeighbors(new Long[]{-2L});
            node2.setNeighbors(new Long[]{-1L, -3L});
            node3.setNeighbors(new Long[]{-2L});
            nodes.addAll(Arrays.asList(node1, node2, node3));

            RoomDTO room1 = new RoomDTO();
            room1.setName((modificator * 100 + floorNum * 10 + 1) + "");
            room1.setPoints(Arrays.asList(
                    new PointDTO(100, 100), new PointDTO(300, 100),
                    new PointDTO(300, 200), new PointDTO(100, 200)
            ));
            room1.setNodeId(-1L);

            RoomDTO room2 = new RoomDTO();
            room2.setName((modificator * 100 + floorNum * 10 + 2) + "");
            room2.setPoints(Arrays.asList(
                    new PointDTO(400, 100), new PointDTO(600, 100),
                    new PointDTO(600, 200), new PointDTO(400, 200)
            ));
            room2.setNodeId(-2L);

            rooms.addAll(Arrays.asList(room1, room2));

            if (floorNum == 1) {
                StairsDTO sDto = new StairsDTO();
                sDto.setPoints(Arrays.asList(
                        new PointDTO(750, 100), new PointDTO(850, 100),
                        new PointDTO(850, 200), new PointDTO(750, 200)
                ));
                sDto.setNodeId(-3L);
                sDto.setStairs(new Long[]{});
                stairs.add(sDto);
            }

            FloorDTO floorData = new FloorDTO(fDto, rooms, stairs, nodes);
            floorService.createFloor(bDto.getId(), floorNum, floorData);
        }
    }
}
