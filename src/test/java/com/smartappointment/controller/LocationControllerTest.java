package com.smartappointment.controller;

import com.smartappointment.BaseIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for {@code /api/locations}.
 *
 * <p>Security rules under test:
 * <ul>
 *   <li>{@code GET} endpoints — any authenticated user.</li>
 *   <li>{@code POST / PUT / DELETE} endpoints — ADMIN only.</li>
 * </ul>
 */
@DisplayName("Location Controller — /api/locations")
class LocationControllerTest extends BaseIntegrationTest {

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        adminToken = getAdminToken();
        userToken = registerAndGetToken("Can", "Yüksel",
                "can_yuksel_" + UUID.randomUUID() + "@hotmail.com", "Test123!");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /locations — ADMIN only
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /locations: admin with valid data → 201 Created")
    void createLocation_shouldReturn201_whenCalledByAdmin() {
        String name = "TestLoc_" + UUID.randomUUID().toString().substring(0, 8);

        given()
                .header("Authorization", bearer(adminToken))
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "%s",
                            "address": "Test Mahallesi No:1",
                            "city": "İstanbul"
                        }
                        """.formatted(name))
                .when()
                .post("/locations")
                .then()
                .statusCode(201)
                .body("success", is(true))
                .body("data.name", equalTo(name))
                .body("data.city", equalTo("İstanbul"));
    }

    @Test
    @DisplayName("POST /locations: regular user → 403 Forbidden")
    void createLocation_shouldReturn403_whenCalledByRegularUser() {
        given()
                .header("Authorization", bearer(userToken))
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "Unauthorized",
                            "address": "Some address",
                            "city": "Ankara"
                        }
                        """)
                .when()
                .post("/locations")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("POST /locations: no token → 401 Unauthorized")
    void createLocation_shouldReturn401_whenNotAuthenticated() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "Unauthorized",
                            "address": "Some address",
                            "city": "Ankara"
                        }
                        """)
                .when()
                .post("/locations")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("POST /locations: missing city → 400 Bad Request")
    void createLocation_shouldReturn400_whenCityIsMissing() {
        given()
                .header("Authorization", bearer(adminToken))
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "TestLoc",
                            "address": "Test address"
                        }
                        """)
                .when()
                .post("/locations")
                .then()
                .statusCode(400)
                .body("success", is(false));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /locations
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /locations: authenticated → 200 with location list")
    void getAllLocations_shouldReturn200_whenAuthenticated() {
        given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/locations")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.content", notNullValue());
    }

    @Test
    @DisplayName("GET /locations: with pagination → 200 paginated response")
    void getAllLocations_shouldReturn200_WithPaginationParams() {
        given()
                .header("Authorization", bearer(userToken))
                .queryParam("page", 1)
                .queryParam("pageSize", 10)
                .when()
                .get("/locations")
                .then()
                .statusCode(200)
                .time(lessThan(MAX_RESPONSE_TIME_MS), RESPONSE_TIME_UNIT)
                .body("success", is(true))
                .body("data.content", notNullValue());
    }

    @Test
    @DisplayName("GET /locations: no token → 401 Unauthorized")
    void getAllLocations_shouldReturn401_whenNotAuthenticated() {
        given()
                .when()
                .get("/locations")
                .then()
                .statusCode(401);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /locations/{id}
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /locations/{id}: existing → 200 with location details")
    void getLocationById_shouldReturn200_whenLocationExists() {
        String name = "GetByIdLoc_" + UUID.randomUUID().toString().substring(0, 8);

        Integer id = given()
                .header("Authorization", bearer(adminToken))
                .contentType(ContentType.JSON)
                .body("""
                        { "name": "%s", "address": "Test Addr", "city": "Ankara" }
                        """.formatted(name))
                .when().post("/locations")
                .then().statusCode(201)
                .extract().path("data.id");

        given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/locations/{id}", id)
                .then()
                .statusCode(200)
                .body("data.id", equalTo(id))
                .body("data.name", equalTo(name));
    }

    @Test
    @DisplayName("GET /locations/{id}: non-existent → 404 Not Found")
    void getLocationById_shouldReturn404_whenLocationDoesNotExist() {
        given()
                .header("Authorization", bearer(adminToken))
                .when()
                .get("/locations/{id}", 999999)
                .then()
                .statusCode(404)
                .body("success", is(false));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /locations/active
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /locations/active: authenticated → 200")
    void getActiveLocations_shouldReturn200_whenAuthenticated() {
        given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/locations/active")
                .then()
                .statusCode(200)
                .body("success", is(true));
    }
}
