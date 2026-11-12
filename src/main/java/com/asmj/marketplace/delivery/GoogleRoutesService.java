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

    public GoogleRouteResult drivingRoute(double originLat, double originLng, double destinationLat, double destinationLng) {
        if (apiKey == null || apiKey.isBlank())
            throw new IllegalStateException("Google Routes API is not configured yet. Please contact the marketplace administrator or choose pickup.");
        validateCoordinate(originLat, originLng);
        validateCoordinate(destinationLat, destinationLng);
        Map<String,Object> body = Map.of(
                "origin", Map.of("location", Map.of("latLng", Map.of("latitude", originLat, "longitude", originLng))),
                "destination", Map.of("location", Map.of("latLng", Map.of("latitude", destinationLat, "longitude", destinationLng))),
                "travelMode", "DRIVE",
                "routingPreference", "TRAFFIC_UNAWARE",
                "units", "METRIC"
        );
        try {
            JsonNode response = client.post().uri("/directions/v2:computeRoutes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Goog-Api-Key", apiKey)
                    .header("X-Goog-FieldMask", "routes.distanceMeters,routes.polyline.encodedPolyline")
                    .body(body).retrieve().body(JsonNode.class);
            JsonNode route = response == null ? null : response.path("routes").path(0);
            JsonNode meters = route == null ? null : route.path("distanceMeters");
            String polyline = route == null ? null : route.path("polyline").path("encodedPolyline").asText(null);
            if (meters == null || !meters.canConvertToLong() || meters.asLong() <= 0 || polyline == null || polyline.isBlank())
                throw new IllegalStateException("Google Maps could not find a driving route for these locations. Please adjust the map pins or choose pickup.");
            BigDecimal km = BigDecimal.valueOf(meters.asLong()).divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);
            return new GoogleRouteResult(km, polyline);
        } catch (org.springframework.web.client.RestClientResponseException ex) {
            throw new IllegalStateException("Google Maps route service returned an error. Please retry or choose pickup. (HTTP " + ex.getStatusCode().value() + ")");
        } catch (org.springframework.web.client.RestClientException ex) {
            throw new IllegalStateException("Unable to reach Google Maps route service. Please retry or choose pickup.");
        }
    }

    private void validateCoordinate(double lat, double lng) {
        if (!Double.isFinite(lat) || !Double.isFinite(lng) || lat < -90 || lat > 90 || lng < -180 || lng > 180)
            throw new IllegalArgumentException("Please select valid vendor and delivery locations on the map.");
    }
}
