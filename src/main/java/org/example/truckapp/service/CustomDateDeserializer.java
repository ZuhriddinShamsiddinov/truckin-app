package org.example.truckapp.service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CustomDateDeserializer extends JsonDeserializer<LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser,
                                     DeserializationContext context) throws IOException {
        var dateTimeStr = jsonParser.getValueAsString();
        return LocalDateTime.parse(dateTimeStr, formatter);
    }

    public static Instant convertInstant(String zoneId, LocalDateTime dateTime) {
        ZoneId zone = ZoneId.of(zoneId);

        ZonedDateTime zonedDateTime = dateTime.atZone(zone);

        return zonedDateTime.toInstant();
    }

    public static String formatDateTime(ZonedDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return dateTime.format(format);
    }
}
