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
 * Integration tests for {@code /api/providers}.
 *
 * <ul>
 *   <li>{@code GET} endpoints — any authenticated user.</li>
 *   <li>{@code POST / PUT / DELETE} endpoints — ADMIN only.</li>
 * </ul>
 *
 * <p>Note: Creating a provider requires a valid {@code categoryId}.
 * The test creates a category first, then uses its ID.
 */
@DisplayName("ServiceProvider Controller — /api/providers")
class ServiceProviderControllerTest extends BaseIntegrationTest {

    private String adminToken;
    private String userToken;
    private Integer categoryId;

    @BeforeEach
    void setUp() {
        adminToken = getAdminToken();
        userToken = registerAndGetToken("Berkan", "Serbes",
                "berkan.serbes_" + UUID.randomUUID() + "@hotmail.com", "Pass123!");

        String categoryName = "Healthcare Services_" + UUID.randomUUID().toString().substring(0, 8);
        categoryId = given()
                .header("Authorization", bearer(adminToken))
                .contentType(ContentType.JSON)
                .body("""
                        { "name": "%s" }
                        """.formatted(categoryName))
                .when().post("/categories")
                .then().statusCode(201)
                .extract().path("data.id");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /providers — ADMIN only
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /providers: admin with valid data → 201 Created")
    void createProvider_shouldReturn201_whenCalledByAdmin() {
        String name = "Dr. Ahmet Yilmaz";

        given()
                .header("Authorization", bearer(adminToken))
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "%s",
                            "title": "Specialist Physician",
                            "phone": "+905551234567",
                            "email": "ahmet.yilmaz@hospital.com",
                            "categoryId": %d
                        }
                        """.formatted(name, categoryId))
                .when()
                .post("/providers")
                .then()
                .statusCode(201)
                .body("success", is(true))
                .body("data.name", equalTo(name))
                .body("data.title", equalTo("Specialist Physician"));
    }

    @Test
    @DisplayName("POST /providers: regular user → 403 Forbidden")
    void createProvider_shouldReturn403_whenCalledByRegularUser() {
        given()
                .header("Authorization", bearer(userToken))
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "Dr. Mehmet Yilmaz",
                            "title": "Specialist Physician",
                            "phone": "+905551234567",
                            "email": "mehmet.yilmaz@hospital.com",
                            "categoryId": %d
                        }
                        """.formatted(categoryId))
                .when()
                .post("/providers")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("POST /providers: no token → 401 Unauthorized")
    void createProvider_shouldReturn401_whenNotAuthenticated() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "Dr. Can Tekin",
                            "title": "Dentist",
                            "phone": "+905551234567",
                            "email": "can.tekin@hospital.com",
                            "categoryId": %d
                        }
                        """.formatted(categoryId))
                .when()
                .post("/providers")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("POST /providers: missing required fields → 400 Bad Request")
    void createProvider_shouldReturn400_whenRequiredFieldsMissing() {
        given()
                .header("Authorization", bearer(adminToken))
                .contentType(ContentType.JSON)
                .body("""
                        { "name": "MissingCategoryId" }
                        """)
                .when()
                .post("/providers")
                .then()
                .statusCode(400)
                .body("success", is(false));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /providers
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /providers: authenticated → 200 with provider list")
    void getAllProviders_shouldReturn200_whenAuthenticated() {
        given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/providers")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.content", notNullValue());
    }

    @Test
    @DisplayName("GET /providers: no token → 401 Unauthorized")
    void getAllProviders_shouldReturn401_whenNotAuthenticated() {
        given()
                .when()
                .get("/providers")
                .then()
                .statusCode(401);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /providers/{id}
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /providers/{id}: existing → 200 with provider details")
    void getProviderById_shouldReturn200_whenProviderExists() {
        String name = "Dr. Fatih Kaya";

        Integer id = given()
                .header("Authorization", bearer(adminToken))
                .contentType(ContentType.JSON)
                .body("""
                        { "name": "%s", "title": "Senior Consultant", "categoryId": %d }
                        """.formatted(name, categoryId))
                .when().post("/providers")
                .then().statusCode(201)
                .extract().path("data.id");

        given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/providers/{id}", id)
                .then()
                .statusCode(200)
                .body("data.id", equalTo(id));
    }

    @Test
    @DisplayName("GET /providers/{id}: non-existent → 404 Not Found")
    void getProviderById_shouldReturn404_whenProviderDoesNotExist() {
        given()
                .header("Authorization", bearer(adminToken))
                .when()
                .get("/providers/{id}", 999999)
                .then()
                .statusCode(404)
                .body("success", is(false));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /providers/active
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /providers/active: authenticated → 200")
    void getActiveProviders_shouldReturn200_whenAuthenticated() {
        given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/providers/active")
                .then()
                .statusCode(200)
                .body("success", is(true));
    }
}
