package com.asmj.marketplace.delivery;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class GoogleRoutesService {
    private final RestClient client;
    @Value("${google.maps.api-key:}") private String apiKey;

    public GoogleRoutesService(RestClient.Builder builder) {
        this.client = builder.baseUrl("https://routes.googleapis.com").build();
    }

    public BigDecimal drivingDistanceKm(String origin, String destination) {
        if (apiKey == null || apiKey.isBlank())
            throw new IllegalStateException("Google Routes API is not configured yet. Please contact the marketplace administrator or choose pickup.");
        if (origin == null || origin.isBlank() || destination == null || destination.isBlank())
            throw new IllegalArgumentException("Complete vendor shop and customer delivery addresses are required to calculate road distance.");
        Map<String,Object> body = Map.of(
                "origin", Map.of("address", origin),
                "destination", Map.of("address", destination),
                "travelMode", "DRIVE",
                "routingPreference", "TRAFFIC_UNAWARE",
                "units", "METRIC"
        );
        try {
            JsonNode response = client.post().uri("/directions/v2:computeRoutes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Goog-Api-Key", apiKey)
                    .header("X-Goog-FieldMask", "routes.distanceMeters")
                    .body(body).retrieve().body(JsonNode.class);
            JsonNode meters = response == null ? null : response.path("routes").path(0).path("distanceMeters");
            if (meters == null || !meters.canConvertToLong() || meters.asLong() < 0)
                throw new IllegalStateException("Google Maps could not find a driving route for these addresses. Please check the address or choose pickup.");
            return BigDecimal.valueOf(meters.asLong()).divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);
        } catch (org.springframework.web.client.RestClientResponseException ex) {
            throw new IllegalStateException("Google Maps distance service returned an error. Please retry or choose pickup. (HTTP " + ex.getStatusCode().value() + ")");
        } catch (org.springframework.web.client.RestClientException ex) {
            throw new IllegalStateException("Unable to reach Google Maps distance service. Please retry or choose pickup.");
        }
    }
}
