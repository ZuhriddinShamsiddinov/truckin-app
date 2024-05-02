package org.example.truckapp.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TruckInfoDto {

    private Long id;

    private Double latitude;

    private Double longitude;

    private BigDecimal totalDistance;

    private Double averageSpeed;

    private String currentCity;

    private Instant sentTime;

    private String zoneId;

    private Boolean last;
}