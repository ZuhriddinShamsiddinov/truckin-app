package org.example.truckapp.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.truckapp.service.CustomDateDeserializer;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDto {
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private LocalDateTime fromDate;
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private LocalDateTime toDate;
    private String zoneId;
}
