package com.smartappointment.controller;

import com.smartappointment.BaseIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for {@code /api/appointments}.
 *
 * <ul>
 *   <li>All appointment endpoints require authentication (401 without token).</li>
 *   <li>{@code GET /appointments/all} and admin-scoped endpoints require ADMIN role (403 for USER).</li>
 *   <li>Regular users can only access their own appointments.</li>
 * </ul>
 */
@DisplayName("Appointment Controller — /api/appointments")
class AppointmentControllerTest extends BaseIntegrationTest {

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        adminToken = getAdminToken();

        String userEmail = "berkan_serbes_" + UUID.randomUUID() + "@hotmail.com";
        userToken = registerAndGetToken("Berkan", "Serbes", userEmail, "Test123!");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /appointments — current user's appointments (paginated)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /appointments: authenticated user → 200 with paged response")
    void getMyAppointments_shouldReturn200_whenAuthenticated() {
        given()
                .header("Authorization", bearer(userToken))
                .queryParam("page", 1)
                .queryParam("pageSize", 10)
                .when()
                .get("/appointments")
                .then()
                .statusCode(200)
                .time(lessThan(MAX_RESPONSE_TIME_MS), RESPONSE_TIME_UNIT)
                .body("success", is(true))
                .body("data.content", notNullValue())
                .body("data.page", equalTo(1))
                .body("data.pageSize", equalTo(10));
    }

    @Test
    @DisplayName("GET /appointments: no token → 401 Unauthorized")
    void getMyAppointments_shouldReturn401_whenNotAuthenticated() {
        given()
                .when()
                .get("/appointments")
                .then()
                .statusCode(401);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /appointments/me/upcoming
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /appointments/me/upcoming: authenticated → 200")
    void getUpcomingAppointments_shouldReturn200_whenAuthenticated() {
        given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/appointments/me/upcoming")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.content", notNullValue());
    }

    @Test
    @DisplayName("GET /appointments/me/upcoming: no token → 401")
    void getUpcomingAppointments_shouldReturn401_whenNotAuthenticated() {
        given()
                .when()
                .get("/appointments/me/upcoming")
                .then()
                .statusCode(401);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /appointments/me/past
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /appointments/me/past: authenticated → 200")
    void getPastAppointments_shouldReturn200_whenAuthenticated() {
        given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/appointments/me/past")
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /appointments/me/count
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /appointments/me/count: authenticated → 200 with count")
    void countMyAppointments_shouldReturn200WithCount_whenAuthenticated() {
        given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/appointments/me/count")
                .then()
                .statusCode(200);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /appointments/all — admin only
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /appointments/all: admin → 200 with all appointments")
    void getAllAppointments_shouldReturn200_whenCalledByAdmin() {
        given()
                .header("Authorization", bearer(adminToken))
                .queryParam("page", 1)
                .queryParam("pageSize", 20)
                .when()
                .get("/appointments/all")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.content", notNullValue());
    }

    @Test
    @DisplayName("GET /appointments/all: regular user → 403 Forbidden")
    void getAllAppointments_shouldReturn403_whenCalledByRegularUser() {
        given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/appointments/all")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("GET /appointments/all: no token → 401 Unauthorized")
    void getAllAppointments_shouldReturn401_whenNotAuthenticated() {
        given()
                .when()
                .get("/appointments/all")
                .then()
                .statusCode(401);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /appointments/status — admin only
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /appointments/status: admin → 200")
    void getAppointmentsByStatus_shouldReturn200_whenCalledByAdmin() {
        given()
                .header("Authorization", bearer(adminToken))
                .queryParam("status", "SCHEDULED")
                .when()
                .get("/appointments/status")
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @DisplayName("GET /appointments/status: regular user → 403")
    void getAppointmentsByStatus_shouldReturn403_whenCalledByRegularUser() {
        given()
                .header("Authorization", bearer(userToken))
                .queryParam("status", "SCHEDULED")
                .when()
                .get("/appointments/status")
                .then()
                .statusCode(403);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /appointments — create appointment
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /appointments: no token → 401 Unauthorized")
    void createAppointment_shouldReturn401_whenNotAuthenticated() {
        String futureTime = LocalDateTime.now().plusDays(7)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "serviceProviderId": 1,
                            "locationId": 1,
                            "startTime": "%s"
                        }
                        """.formatted(futureTime))
                .when()
                .post("/appointments")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("POST /appointments: past date → 400 Bad Request (@Future validation)")
    void createAppointment_shouldReturn400_whenStartTimeIsInThePast() {
        String pastTime = LocalDateTime.now().minusDays(1)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        given()
                .header("Authorization", bearer(userToken))
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "serviceProviderId": 1,
                            "locationId": 1,
                            "startTime": "%s"
                        }
                        """.formatted(pastTime))
                .when()
                .post("/appointments")
                .then()
                .statusCode(400)
                .body("success", is(false));
    }

    @Test
    @DisplayName("POST /appointments: missing required fields → 400 Bad Request")
    void createAppointment_shouldReturn400_whenRequiredFieldsMissing() {
        given()
                .header("Authorization", bearer(userToken))
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/appointments")
                .then()
                .statusCode(400)
                .body("success", is(false));
    }
}
