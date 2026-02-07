package org.mininuniver.interactiveMap.repository;

import org.mininuniver.interactiveMap.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
}
