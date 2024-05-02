package org.example.truckapp.repository;

import java.time.Instant;
import java.util.List;

import org.example.truckapp.dto.TruckDto;
import org.example.truckapp.entity.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TruckRepository extends JpaRepository<Truck, Long> {
    @Query("select new org.example.truckapp.dto.TruckDto(t, ti) " +
            "from TruckInfo ti " +
            "join fetch ti.truck t " +
            "where ti.last = true and t.id=:id")
    TruckDto getTruckDtoById(@Param("id") Long id);
}
