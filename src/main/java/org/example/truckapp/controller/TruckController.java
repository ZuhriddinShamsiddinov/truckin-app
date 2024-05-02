package org.example.truckapp.controller;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.example.truckapp.dto.SearchDto;
import org.example.truckapp.dto.TruckDto;
import org.example.truckapp.dto.TruckInfoDto;
import org.example.truckapp.service.TruckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TruckController {
    private final TruckService truckService;

    /**
     * This method for logging about truck state for each minute
     * @param id - truck
     * @param dto - all infos
     * @return
     */
    @PostMapping("/api/v1/truck/info/{id}")
    public ResponseEntity<Long> createInfo(@PathVariable Long id,
                                           @RequestBody TruckInfoDto dto) {
        return ResponseEntity.ok(truckService.createInfo(id, dto));
    }

    @GetMapping("/api/v1/truck/{id}")
    public ResponseEntity<TruckDto> get(@PathVariable Long id) {
        return truckService.get(id);
    }

    @PutMapping("/api/v1/truck")
    public ResponseEntity<byte[]> getAll(
            @RequestBody SearchDto dto) {
        return truckService.getAll(dto);
    }
}
