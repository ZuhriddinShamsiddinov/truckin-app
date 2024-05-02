package org.example.truckapp.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import static org.example.truckapp.OpenSourceUtil.calculateDistance;
import static org.example.truckapp.OpenSourceUtil.calculateSpeed;
import static org.example.truckapp.OpenSourceUtil.fetchCityNameAsync;
import static org.example.truckapp.OpenSourceUtil.fetchZoneIdAsync;
import static org.example.truckapp.OpenSourceUtil.getTimeZoneId;
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

                try {
                    TruckInfo info = getTruckInfo(dto, truck, calculateDistance, last, sendTime);

                    TruckInfo savedInfo = truckInfoRepository.saveAll(List.of(last, info)).get(1);

                    truck.setTotalDistance(truck.getTotalDistance().add(info.getTotalDistance()));
                    truckRepository.save(truck);

                    return savedInfo.getId();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

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
                                          Instant sendTime) throws Exception {
        AtomicReference<String> address = new AtomicReference<>();
        CompletableFuture<Void> futureAddress = fetchCityNameAsync(dto.getLatitude(), dto.getLongitude())
                .thenAcceptAsync(address::set);

        AtomicReference<String> zoneId = new AtomicReference<>();
        CompletableFuture<Void> futureZoneId = fetchZoneIdAsync(dto.getLatitude(), dto.getLongitude())
                .thenAcceptAsync(zoneId::set);

        futureZoneId.get();
        futureAddress.get();

        TruckInfo info = TruckInfo.builder()
                .truck(truck)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .totalDistance(BigDecimal.valueOf(calculateDistance))
                .averageSpeed(calculateSpeed(last.getSentTime(), sendTime, calculateDistance))
                .currentCity(address.get())
                .sentTime(sendTime)
                .zoneId(zoneId.get())
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
            String zoneId = getTimeZoneId(dto.getLatitude(), dto.getLongitude());
            Instant fromDate = CustomDateDeserializer.convertInstant(zoneId, dto.getFromDate());
            Instant toDate = CustomDateDeserializer.convertInstant(zoneId, dto.getToDate());
            List<TruckDto> dtoList = truckInfoRepository.getTruckDtosWithTotalDistance(fromDate, toDate, zoneId);

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


}
