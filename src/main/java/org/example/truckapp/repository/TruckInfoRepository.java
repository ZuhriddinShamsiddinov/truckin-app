package org.example.truckapp.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.example.truckapp.dto.TruckDto;
import org.example.truckapp.entity.TruckInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TruckInfoRepository extends JpaRepository<TruckInfo, Long> {
    Optional<TruckInfo> findByLastTrueAndTruckId(Long truck_id);

    @Query("select new org.example.truckapp.dto.TruckDto(t, ti,:zoneId) " +
            "from TruckInfo ti " +
            "join fetch ti.truck t " +
            "where ti.last = true and ti.sentTime between :fromDate and :toDate")
    List<TruckDto> getTruckDtosWithTotalDistance(@Param("fromDate") Instant fromDate,
                                                 @Param("toDate") Instant toDate,
                                                 @Param("zoneId")String zoneId);
}