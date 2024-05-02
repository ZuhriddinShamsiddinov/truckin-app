package org.example.truckapp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import lombok.RequiredArgsConstructor;
import org.example.truckapp.entity.Truck;
import org.example.truckapp.entity.TruckInfo;
import org.example.truckapp.repository.TruckInfoRepository;
import org.example.truckapp.repository.TruckRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
@OpenAPIDefinition
public class TruckAppApplication {
    private final TruckRepository truckRepository;
    private final TruckInfoRepository truckInfoRepository;

    public static void main(String[] args) {
        SpringApplication.run(TruckAppApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Truck truck1 = Truck.builder()
                    .number("AS100000")
                    .active(true)
                    .totalDistance(BigDecimal.valueOf(9273.35))
                    .build();

            Truck truck2 = Truck.builder()
                    .number("As20202")
                    .active(true)
                    .totalDistance(BigDecimal.valueOf(9273.35))
                    .build();

            List<Truck> trucks = truckRepository.saveAll(List.of(truck1, truck2));

            TruckInfo info1 = TruckInfo.builder()
                    .truck(trucks.get(0))
                    .latitude(41.0141332553158)
                    .longitude(-73.6403156309893)
                    .totalDistance(BigDecimal.valueOf(1571.06))
                    .averageSpeed(10.006723024994965)
                    .currentCity("New_York")
                    .sentTime(Instant.parse("2024-05-02T05:44:25.040Z"))
                    .zoneId("America/New_York")
                    .last(true)
                    .build();


            TruckInfo info2 = TruckInfo.builder()
                    .truck(trucks.get(0))
                    .latitude(41.025786781169664)
                    .longitude(-73.6297272779879)
                    .totalDistance(BigDecimal.valueOf(7702.29))
                    .averageSpeed(29.286273318396837)
                    .currentCity("New_York")
                    .sentTime(Instant.parse("2024-05-02T05:41:47.195Z"))
                    .zoneId("America/New_York")
                    .last(false)
                    .build();

            TruckInfo info3 = TruckInfo.builder()
                    .truck(trucks.get(1))
                    .latitude(41.0141332553158)
                    .longitude(-73.6403156309893)
                    .totalDistance(BigDecimal.valueOf(1571.06))
                    .averageSpeed(10.006723024994965)
                    .currentCity("New_York")
                    .sentTime(Instant.parse("2024-05-02T05:44:25.040Z"))
                    .zoneId("America/New_York")
                    .last(true)
                    .build();


            TruckInfo info4 = TruckInfo.builder()
                    .truck(trucks.get(1))
                    .latitude(41.025786781169664)
                    .longitude(-73.6297272779879)
                    .totalDistance(BigDecimal.valueOf(7702.29))
                    .averageSpeed(29.286273318396837)
                    .currentCity("New_York")
                    .sentTime(Instant.parse("2024-05-02T05:41:47.195Z"))
                    .zoneId("America/New_York")
                    .last(false)
                    .build();

            truckInfoRepository.saveAll(List.of(info1, info2, info3, info4));
        };
    }
}
