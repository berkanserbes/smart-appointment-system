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
 * Integration tests for {@code /api/feedbacks}.
 */
@DisplayName("Feedback Controller — /api/feedbacks")
class FeedbackControllerTest extends BaseIntegrationTest {

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        adminToken = getAdminToken();
        userToken = registerAndGetToken("Caner", "Demir",
                "caner_demir_" + UUID.randomUUID() + "@hotmail.com", "Test123!");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /feedbacks — current user's feedbacks
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /feedbacks: authenticated user → 200 with paginated list")
    void getMyFeedbacks_shouldReturn200_whenAuthenticated() {
        given()
                .header("Authorization", bearer(userToken))
                .queryParam("page", 1)
                .queryParam("pageSize", 10)
                .when()
                .get("/feedbacks")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.content", notNullValue());
    }

    @Test
    @DisplayName("GET /feedbacks: no token → 401 Unauthorized")
    void getMyFeedbacks_shouldReturn401_whenNotAuthenticated() {
        given()
                .when()
                .get("/feedbacks")
                .then()
                .statusCode(401);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /feedbacks/{id}
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /feedbacks/{id}: non-existent ID → 404 Not Found")
    void getFeedbackById_shouldReturn404_whenFeedbackDoesNotExist() {
        given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/feedbacks/{id}", 999999)
                .then()
                .statusCode(404)
                .body("success", is(false));
    }

    @Test
    @DisplayName("GET /feedbacks/{id}: no token → 401 Unauthorized")
    void getFeedbackById_shouldReturn401_whenNotAuthenticated() {
        given()
                .when()
                .get("/feedbacks/{id}", 1)
                .then()
                .statusCode(401);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /feedbacks — validation tests (no real appointment available)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /feedbacks: no token → 401 Unauthorized")
    void createFeedback_shouldReturn401_whenNotAuthenticated() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "appointmentId": 1,
                            "rating": 5,
                            "comment": "Harika hizmet"
                        }
                        """)
                .when()
                .post("/feedbacks")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("POST /feedbacks: rating out of range (0) → 400 Bad Request")
    void createFeedback_shouldReturn400_whenRatingBelowMinimum() {
        given()
                .header("Authorization", bearer(userToken))
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "appointmentId": 1,
                            "rating": 0
                        }
                        """)
                .when()
                .post("/feedbacks")
                .then()
                .statusCode(400)
                .body("success", is(false));
    }

    @Test
    @DisplayName("POST /feedbacks: rating out of range (6) → 400 Bad Request")
    void createFeedback_shouldReturn400_whenRatingAboveMaximum() {
        given()
                .header("Authorization", bearer(userToken))
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "appointmentId": 1,
                            "rating": 6
                        }
                        """)
                .when()
                .post("/feedbacks")
                .then()
                .statusCode(400)
                .body("success", is(false));
    }

    @Test
    @DisplayName("POST /feedbacks: missing appointmentId → 400 Bad Request")
    void createFeedback_shouldReturn400_whenAppointmentIdIsMissing() {
        given()
                .header("Authorization", bearer(userToken))
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "rating": 4,
                            "comment": "Good service"
                        }
                        """)
                .when()
                .post("/feedbacks")
                .then()
                .statusCode(400)
                .body("success", is(false));
    }

    @Test
    @DisplayName("POST /feedbacks: missing rating → 400 Bad Request")
    void createFeedback_shouldReturn400_whenRatingIsMissing() {
        given()
                .header("Authorization", bearer(userToken))
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "appointmentId": 1,
                            "comment": "Missing rating"
                        }
                        """)
                .when()
                .post("/feedbacks")
                .then()
                .statusCode(400)
                .body("success", is(false));
    }
}
