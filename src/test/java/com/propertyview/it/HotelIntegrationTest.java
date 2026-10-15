package com.propertyview.it;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.datasource.url=jdbc:h2:mem:e2e;DB_CLOSE_DELAY=-1")
class HotelApiEndToEndTest {

    @Value("${local.server.port}")
    int port;

    private final HttpClient http = HttpClient.newHttpClient();

    private static final String DOUBLETREE = """
            {
              "name": "DoubleTree by Hilton Minsk",
              "description": "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms in the Belorussian capital",
              "brand": "Hilton",
              "address": {"houseNumber": 9, "street": "Pobediteley Avenue", "city": "Minsk",
                          "country": "Belarus", "postCode": "220004"},
              "contacts": {"phone": "+375 17 309-80-00", "email": "doubletreeminsk.info@hilton.com"},
              "arrivalTime": {"checkIn": "14:00", "checkOut": "12:00"}
            }
            """;

    private static final String MOSCOW_HILTON = """
            {
              "name": "Hilton Moscow Leningradskaya", "brand": "Hilton",
              "address": {"houseNumber": 21, "street": "Kalanchevskaya", "city": "Moscow",
                          "country": "Russia", "postCode": "107140"},
              "contacts": {"phone": "+7 495 627-55-50", "email": "moscow@hilton.com"},
              "arrivalTime": {"checkIn": "15:00"}
            }
            """;

    private static final List<String> TASK_AMENITIES = List.of(
        "Free parking", "Free WiFi", "Non-smoking rooms", "Concierge", "On-site restaurant",
        "Fitness center", "Pet-friendly rooms", "Room service", "Business center", "Meeting rooms");

    private String url(String path) { return "http://localhost:" + port + "/property-view" + path; }

    private HttpResponse<String> get(String path) throws Exception {
        return http.send(HttpRequest.newBuilder(URI.create(url(path))).GET().build(),
            HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String path, String json) throws Exception {
        return http.send(HttpRequest.newBuilder(URI.create(url(path)))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)).build(),
            HttpResponse.BodyHandlers.ofString());
    }

    private static DocumentContext json(HttpResponse<String> response) { return JsonPath.parse(response.body()); }

    private static String jsonArray(List<String> items) {
        return items.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(",", "[", "]"));
    }

    @Test
    void scenarioFromTheTask() throws Exception {
        // empty database
        HttpResponse<String> empty = get("/hotels");
        assertThat(empty.statusCode()).isEqualTo(200);
        assertThat(empty.body()).isEqualTo("[]");

        // POST /hotels
        HttpResponse<String> created = post("/hotels", DOUBLETREE);
        assertThat(created.statusCode()).isEqualTo(201);
        assertThat(created.headers().firstValue("Location").orElseThrow()).endsWith("/property-view/hotels/1");
        DocumentContext c = json(created);
        assertThat(c.read("$.id", Integer.class)).isEqualTo(1);
        assertThat(c.read("$.address", String.class)).isEqualTo("9 Pobediteley Avenue, Minsk, 220004, Belarus");
        assertThat(c.read("$.phone", String.class)).isEqualTo("+375 17 309-80-00");

        assertThat(json(post("/hotels", MOSCOW_HILTON)).read("$.id", Integer.class)).isEqualTo(2);

        // POST /hotels/{id}/amenities
        HttpResponse<String> amenities = post("/hotels/1/amenities", jsonArray(TASK_AMENITIES));
        assertThat(amenities.statusCode()).isEqualTo(200);
        assertThat(json(amenities).read("$.amenities", List.class)).isEqualTo(TASK_AMENITIES);
        assertThat(post("/hotels/2/amenities", "[\"free wifi\", \"Pool\"]").statusCode()).isEqualTo(200);

        // GET /hotels/{id}
        DocumentContext details = json(get("/hotels/1"));
        assertThat(details.read("$.brand", String.class)).isEqualTo("Hilton");
        assertThat(details.read("$.address.houseNumber", Integer.class)).isEqualTo(9);
        assertThat(details.read("$.address.postCode", String.class)).isEqualTo("220004");
        assertThat(details.read("$.contacts.email", String.class)).isEqualTo("doubletreeminsk.info@hilton.com");
        assertThat(details.read("$.arrivalTime.checkIn", String.class)).isEqualTo("14:00");
        assertThat(details.read("$.arrivalTime.checkOut", String.class)).isEqualTo("12:00");
        assertThat(details.read("$.amenities.length()", Integer.class)).isEqualTo(10);
        assertThat(json(get("/hotels/2")).read("$.amenities", List.class)).containsExactly("Free WiFi", "Pool");

        // GET /hotels
        assertThat(json(get("/hotels")).read("$.length()", Integer.class)).isEqualTo(2);

        // GET /search
        assertThat(json(get("/search?city=minsk")).read("$[*].name", List.class)).containsExactly("DoubleTree by Hilton Minsk");
        assertThat(json(get("/search?brand=hilton")).read("$.length()", Integer.class)).isEqualTo(2);
        assertThat(json(get("/search?name=double&country=belarus")).read("$.length()", Integer.class)).isEqualTo(1);
        assertThat(json(get("/search?amenities=free%20wifi")).read("$.length()", Integer.class)).isEqualTo(2);
        assertThat(json(get("/search?amenities=Free%20WiFi&amenities=Concierge")).read("$.length()", Integer.class)).isEqualTo(1);
        assertThat(json(get("/search?amenities=Free%20WiFi,Pool")).read("$.length()", Integer.class)).isEqualTo(1);
        assertThat(get("/search?city=nowhere").body()).isEqualTo("[]");
        assertThat(json(get("/search")).read("$.length()", Integer.class)).isEqualTo(2);

        // GET /histogram/{param}
        Map<String, Object> byCity = json(get("/histogram/city")).read("$", Map.class);
        assertThat(byCity).containsEntry("Minsk", 1).containsEntry("Moscow", 1).hasSize(2);
        assertThat(json(get("/histogram/brand")).read("$", Map.class)).containsEntry("Hilton", 2);
        assertThat(json(get("/histogram/country")).read("$", Map.class)).containsEntry("Belarus", 1).containsEntry("Russia", 1);
        Map<String, Object> byAmenity = json(get("/histogram/amenities")).read("$", Map.class);
        assertThat(byAmenity).containsEntry("Free WiFi", 2).containsEntry("Pool", 1).containsEntry("Concierge", 1).hasSize(11);
    }

    @Test
    void everythingLivesUnderThePropertyViewPrefix() throws Exception {
        HttpResponse<String> withoutPrefix = http.send(
            HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/hotels")).GET().build(),
            HttpResponse.BodyHandlers.ofString());
        assertThat(withoutPrefix.statusCode()).isEqualTo(404);
        assertThat(get("/hotels").statusCode()).isEqualTo(200);
    }

    @Test
    void errorsAreProblemDetails() throws Exception {
        HttpResponse<String> notFound = get("/hotels/999");
        assertThat(notFound.statusCode()).isEqualTo(404);
        assertThat(notFound.headers().firstValue("Content-Type").orElse("")).contains("application/problem+json");

        HttpResponse<String> invalid = post("/hotels", "{\"name\": \" \"}");
        assertThat(invalid.statusCode()).isEqualTo(400);
        assertThat(json(invalid).read("$.errors.name", String.class)).isNotBlank();

        HttpResponse<String> unknownParam = get("/histogram/stars");
        assertThat(unknownParam.statusCode()).isEqualTo(400);
        assertThat(json(unknownParam).read("$.detail", String.class)).contains("brand, city, country, amenities");

        assertThat(post("/hotels/999/amenities", "[\"x\"]").statusCode()).isEqualTo(404);
    }

    @Test
    void swaggerAndOpenApiAreAvailable() throws Exception {
        HttpResponse<String> docs = get("/v3/api-docs");
        assertThat(docs.statusCode()).isEqualTo(200);
        assertThat(docs.body()).contains("/hotels/{id}/amenities", "/search", "/histogram/{param}");
    }
}
