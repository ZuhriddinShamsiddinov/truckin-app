package org.example.truckapp.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.truckapp.dto.SearchDto;
import org.example.truckapp.dto.TruckDto;
import org.example.truckapp.dto.TruckInfoDto;
import org.example.truckapp.entity.Truck;
import org.example.truckapp.entity.TruckInfo;
import org.example.truckapp.repository.TruckInfoRepository;
import org.example.truckapp.repository.TruckRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class TruckService {
    private final TruckRepository truckRepository;
    private final TruckInfoRepository truckInfoRepository;
    private final ExecutorService executorService;
    private static final double EARTH_RADIUS_METERS = 6371000.0;

    public TruckService(TruckRepository truckRepository,
                        TruckInfoRepository truckInfoRepository) {
        this.truckRepository = truckRepository;
        this.truckInfoRepository = truckInfoRepository;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @PostConstruct
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdown));
    }

    public Long createInfo(Long id, TruckInfoDto dto) {
        CompletableFuture<Long> futureId = CompletableFuture.supplyAsync(() -> {
            Optional<Truck> optionalTruck = truckRepository.findById(id);
            if (optionalTruck.isPresent()) {
                Truck truck = optionalTruck.get();
                TruckInfo last = getLast(id);

                Instant sendTime = Instant.now();

                double calculateDistance = calculateDistance(last.getLatitude(),
                        last.getLongitude(),
                        dto.getLatitude(),
                        dto.getLongitude());

                String zoneId = dto.getZoneId();

                TruckInfo info = getTruckInfo(dto, truck, calculateDistance, last, sendTime, zoneId);

                TruckInfo savedInfo = truckInfoRepository.saveAll(List.of(last, info)).get(1); // Get the saved TruckInfo

                truck.setTotalDistance(truck.getTotalDistance().add(info.getTotalDistance()));
                truckRepository.save(truck);

                return savedInfo.getId();
            } else {
                throw new RuntimeException("Truck not found");
            }
        }, executorService);

        return futureId.join();
    }

    private static TruckInfo getTruckInfo(TruckInfoDto dto,
                                          Truck truck,
                                          double calculateDistance,
                                          TruckInfo last,
                                          Instant sendTime,
                                          String zoneId) {
        TruckInfo info = TruckInfo.builder()
                .truck(truck)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .totalDistance(BigDecimal.valueOf(calculateDistance))
                .averageSpeed(calculateSpeed(last.getSentTime(), sendTime, calculateDistance))
                .currentCity(getCurrentCity(zoneId))
                .sentTime(sendTime)
                .zoneId(zoneId)
                .last(true)
                .build();

        last.setLast(false);
        return info;
    }


    @Transactional(readOnly = true)
    public ResponseEntity<TruckDto> get(Long id) {
        TruckDto dto = truckRepository.getTruckDtoById(id);
        return ResponseEntity.ok(dto);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> getAll(SearchDto dto) {
        CompletableFuture<ResponseEntity<byte[]>> future = CompletableFuture.supplyAsync(() -> {
            Instant fromDate = CustomDateDeserializer.convertInstant(dto.getZoneId(), dto.getFromDate());
            Instant toDate = CustomDateDeserializer.convertInstant(dto.getZoneId(), dto.getToDate());
            List<TruckDto> dtoList = truckInfoRepository.getTruckDtosWithTotalDistance(fromDate, toDate, dto.getZoneId());

            try {
                byte[] bytes = PdfService.generatePdf(dtoList);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "truck_information.pdf");

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, executorService);

        return future.join();
    }

    private TruckInfo getLast(Long truckId) {
        return truckInfoRepository.findByLastTrueAndTruckId(truckId)
                .orElseThrow(() -> new RuntimeException("Info not found"));
    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double sinHalfLatDiff = Math.sin((lat2Rad - lat1Rad) / 2);
        double sinHalfLonDiff = Math.sin((lon2Rad - lon1Rad) / 2);
        double a = sinHalfLatDiff * sinHalfLatDiff +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) * sinHalfLonDiff * sinHalfLonDiff;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }

    private static double calculateSpeed(Instant oldSentTime,
                                         Instant newSentTime,
                                         double distance) {
        Duration duration = Duration.between(oldSentTime, newSentTime);
        return distance / duration.getSeconds();
    }

    private static String getCurrentCity(String zoneId) {
        String[] split = zoneId.split("/");
        return Optional.ofNullable(split[1]).orElse("");
    }

    public static String convertZonedDateTime(Instant instant, String zoneId) {
        ZoneId zone = ZoneId.of(zoneId);
        ZonedDateTime zonedDateTime = instant.atZone(zone);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return zonedDateTime.format(format);
    }
}
