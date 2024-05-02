package org.example.truckapp.entity;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "truck_info")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TruckInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Truck truck;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "total_distance")
    private BigDecimal totalDistance;

    @Column(name = "average_speed")
    private Double averageSpeed;

    @Column(name = "current_city")
    private String currentCity;

    @Column(name = "sent_time")
    private Instant sentTime;

    @Column(name = "zone_id")
    private String zoneId;

    @Column(name = "last")
    private Boolean last;
}