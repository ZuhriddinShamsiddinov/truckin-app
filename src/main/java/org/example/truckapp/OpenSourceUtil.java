package org.example.truckapp;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class OpenSourceUtil {
    private static final double EARTH_RADIUS_METERS = 6371000.0;
    private static final String GEONAMES_USERNAME = "zuhriddin";

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
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

    public static double calculateSpeed(Instant oldSentTime,
                                        Instant newSentTime,
                                        double distance) {
        Duration duration = Duration.between(oldSentTime, newSentTime);
        return distance / duration.getSeconds();
    }

    public static CompletableFuture<String> fetchCityNameAsync(double latitude, double longitude) {
        return CompletableFuture.supplyAsync(() -> getCityFromCoordinates(latitude, longitude));
    }

    public static CompletableFuture<String> fetchZoneIdAsync(double latitude, double longitude) {
        return CompletableFuture.supplyAsync(() -> getTimeZoneId(latitude, longitude));
    }

    public static String getCityFromCoordinates(double latitude, double longitude) {
        String apiUrl = String.format("https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f",
                latitude, longitude);

        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(apiUrl, String.class);

        return parseCityFromJson(jsonResponse);
    }

    public static String parseCityFromJson(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

            JsonNode addressNode = jsonNode.get("address");
            if (addressNode != null && addressNode.has("town")) {
                String road = addressNode.has("road") ? addressNode.get("road").asText() : "";
                String town = addressNode.has("town") ? addressNode.get("town").asText() : "";
                String country = addressNode.has("country") ? addressNode.get("country").asText() : "";

                return road + "," + town + ", " + country;
            }
        } catch (Exception e) {
            log.error("Something wrong... {}", e.getLocalizedMessage());
        }
        return null;
    }

    public static String getTimeZoneId(double latitude, double longitude) {
        String apiUrl = String.format("http://api.geonames.org/timezoneJSON?lat=%f&lng=%f&username=%s", latitude, longitude, GEONAMES_USERNAME);

        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(apiUrl, String.class);

        return parseTimeZoneIdFromJson(jsonResponse);
    }

    public static String parseTimeZoneIdFromJson(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

            if (jsonNode.has("timezoneId")) {
                return jsonNode.get("timezoneId").asText();
            }
        } catch (Exception e) {
            log.error("Something error ...{}", e.getLocalizedMessage());
        }
        return null;
    }

    public static String convertZonedDateTime(Instant instant, String zoneId) {
        ZoneId zone = ZoneId.of(zoneId);
        ZonedDateTime zonedDateTime = instant.atZone(zone);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return zonedDateTime.format(format);
    }
}
