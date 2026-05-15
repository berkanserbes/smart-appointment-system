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
 * Integration tests for {@code /api/categories}.
 *
 * <p>Security rules under test:
 * <ul>
 *   <li>{@code GET} endpoints — any authenticated user.</li>
 *   <li>{@code POST / PUT / DELETE} endpoints — ADMIN only.</li>
 * </ul>
 */
@DisplayName("Category Controller — /api/categories")
class CategoryControllerTest extends BaseIntegrationTest {

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        adminToken = getAdminToken();
        userToken = registerAndGetToken("Ahmet", "Uslu",
                "ahmet_uslu_" + UUID.randomUUID() + "@hotmail.com", "Test123!");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /categories
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /categories: authenticated → 200 with category list")
    void getAllCategories_shouldReturn200_whenAuthenticated() {
        given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/categories")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.content", notNullValue());
    }

    @Test
    @DisplayName("GET /categories: with pagination → 200 paginated response")
    void getAllCategories_shouldReturn200_WithPaginationParams() {
        given()
                .header("Authorization", bearer(userToken))
                .queryParam("page", 1)
                .queryParam("pageSize", 10)
                .when()
                .get("/categories")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.content", notNullValue());
    }

    @Test
    @DisplayName("GET /categories/active: authenticated → 200")
    void getActiveCategories_shouldReturn200_whenAuthenticated() {
        given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/categories/active")
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @DisplayName("GET /categories: no token → 401 Unauthorized")
    void getAllCategories_shouldReturn401_whenNotAuthenticated() {
        given()
                .when()
                .get("/categories")
                .then()
                .statusCode(401);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /categories — ADMIN only
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /categories: admin with valid data → 201 Created")
    void createCategory_shouldReturn201_whenCalledByAdmin() {
        String name = "TestCategory_" + UUID.randomUUID().toString().substring(0, 8);

        given()
                .header("Authorization", bearer(adminToken))
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "%s",
                            "description": "Integration test category",
                            "defaultDurationMinutes": 45
                        }
                        """.formatted(name))
                .when()
                .post("/categories")
                .then()
                .statusCode(201)
                .body("success", is(true))
                .body("data.name", equalTo(name))
                .body("data.defaultDurationMinutes", equalTo(45));
    }

    @Test
    @DisplayName("POST /categories: regular user → 403 Forbidden")
    void createCategory_shouldReturn403_whenCalledByRegularUser() {
        given()
                .header("Authorization", bearer(userToken))
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "ShouldBeRejected",
                            "description": "Should not be created"
                        }
                        """)
                .when()
                .post("/categories")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("POST /categories: no token → 401 Unauthorized")
    void createCategory_shouldReturn401_whenNotAuthenticated() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "Unauthenticated",
                            "description": "Should not be created"
                        }
                        """)
                .when()
                .post("/categories")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("POST /categories: missing name → 400 Bad Request")
    void createCategory_shouldReturn400_whenNameIsMissing() {
        given()
                .header("Authorization", bearer(adminToken))
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "description": "No name provided"
                        }
                        """)
                .when()
                .post("/categories")
                .then()
                .statusCode(400)
                .body("success", is(false));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /categories/{id} and GET /categories/name/{name}
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /categories/{id}: existing category → 200")
    void getCategoryById_shouldReturn200_whenCategoryExists() {
        String name = "GetByIdCat_" + UUID.randomUUID().toString().substring(0, 8);

        // Create first
        Integer id = given()
                .header("Authorization", bearer(adminToken))
                .contentType(ContentType.JSON)
                .body("""
                        { "name": "%s" }
                        """.formatted(name))
                .when().post("/categories")
                .then().statusCode(201)
                .extract().path("data.id");

        // Then retrieve by id
        given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/categories/{id}", id)
                .then()
                .statusCode(200)
                .body("data.id", equalTo(id))
                .body("data.name", equalTo(name));
    }

    @Test
    @DisplayName("GET /categories/{id}: non-existent → 404 Not Found")
    void getCategoryById_shouldReturn404_whenCategoryDoesNotExist() {
        given()
                .header("Authorization", bearer(adminToken))
                .when()
                .get("/categories/{id}", 999999)
                .then()
                .statusCode(404)
                .body("success", is(false));
    }
}
