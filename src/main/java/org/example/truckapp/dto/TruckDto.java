package org.example.truckapp.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import static org.example.truckapp.OpenSourceUtil.convertZonedDateTime;
import org.example.truckapp.entity.Truck;
import org.example.truckapp.entity.TruckInfo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TruckDto {

    private Long id;

    private String number;

    private String currentPosition;

    private BigDecimal lastTotalDistance;

    private BigDecimal totalDistance;

    private String lastSentTime;

    private Long lastAverageSpeed;

    public TruckDto(final Truck truck, final TruckInfo info) {
        this.id = truck.getId();
        this.number = truck.getNumber();
        this.currentPosition = info.getCurrentCity();
        this.lastTotalDistance = info.getTotalDistance().divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);
        this.totalDistance = truck.getTotalDistance().divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);
        this.lastSentTime = convertZonedDateTime(info.getSentTime(), info.getZoneId());
        this.lastAverageSpeed = Math.round(info.getAverageSpeed() * 3.6);
    }

    public TruckDto(final Truck truck, final TruckInfo info, final String zoneId) {
        this.id = truck.getId();
        this.number = truck.getNumber();
        this.currentPosition = info.getCurrentCity();
        this.lastTotalDistance = info.getTotalDistance().divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);
        this.totalDistance = truck.getTotalDistance().divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);
        this.lastSentTime = convertZonedDateTime(info.getSentTime(), zoneId);
        this.lastAverageSpeed = Math.round(info.getAverageSpeed() * 3.6);
    }

}
