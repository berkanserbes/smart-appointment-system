package com.smartappointment.controller;

import com.smartappointment.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for {@code /api/users}.
 *
 * <p>Security rules under test:
 * <ul>
 *   <li>{@code GET /users}, {@code GET /users/active}, {@code GET /users/inactive},
 *       {@code GET /users/role/{role}}, {@code GET /users/search} — ADMIN only.</li>
 *   <li>{@code GET /users/me} — any authenticated user (returns own profile).</li>
 *   <li>{@code GET /users/{id}} — any authenticated user.</li>
 *   <li>All endpoints require authentication (401 without token).</li>
 * </ul>
 */
@DisplayName("User Controller — /api/users")
class UserControllerTest extends BaseIntegrationTest {

    private String adminToken;
    private String userToken;
    private String userEmail;

    @BeforeEach
    void setUp() {
        adminToken = getAdminToken();

        userEmail = "user_" + UUID.randomUUID() + "@test.com";
        userToken = registerAndGetToken("Normal", "User", userEmail, "Test123!");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /users/me
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /users/me: authenticated user → 200 with own profile")
    void getCurrentUser_shouldReturn200WithOwnProfile_whenAuthenticated() {
        given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/users/me")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.email", equalTo(userEmail))
                .body("data.role", equalTo("USER"))
                .body("data.active", is(true));
    }

    @Test
    @DisplayName("GET /users/me: admin token → 200 with admin profile")
    void getCurrentUser_shouldReturnAdminProfile_whenCalledByAdmin() {
        given()
                .header("Authorization", bearer(adminToken))
                .when()
                .get("/users/me")
                .then()
                .statusCode(200)
                .body("data.email", equalTo("admin@smartappointment.com"))
                .body("data.role", equalTo("ADMIN"));
    }

    @Test
    @DisplayName("GET /users/me: no token → 401 Unauthorized")
    void getCurrentUser_shouldReturn401_whenNotAuthenticated() {
        given()
                .when()
                .get("/users/me")
                .then()
                .statusCode(401);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /users — ADMIN only
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /users: admin → 200 with paginated user list")
    void getAllUsers_shouldReturn200WithList_whenCalledByAdmin() {
        given()
                .header("Authorization", bearer(adminToken))
                .queryParam("page", 1)
                .queryParam("pageSize", 20)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.content", notNullValue())
                .body("data.content.size()", greaterThanOrEqualTo(1)); // at least admin
    }

    @Test
    @DisplayName("GET /users: regular user → 403 Forbidden")
    void getAllUsers_shouldReturn403_whenCalledByRegularUser() {
        given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/users")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("GET /users: no token → 401 Unauthorized")
    void getAllUsers_shouldReturn401_whenNotAuthenticated() {
        given()
                .when()
                .get("/users")
                .then()
                .statusCode(401);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /users/active — ADMIN only
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /users/active: admin → 200")
    void getActiveUsers_shouldReturn200_whenCalledByAdmin() {
        given()
                .header("Authorization", bearer(adminToken))
                .when()
                .get("/users/active")
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @DisplayName("GET /users/active: regular user → 403")
    void getActiveUsers_shouldReturn403_whenCalledByRegularUser() {
        given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/users/active")
                .then()
                .statusCode(403);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /users/{id}
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /users/{id}: valid ID → 200 with user details")
    void getUserById_shouldReturn200_whenUserExists() {
        // Retrieve the id of the currently authenticated user
        Integer userId = given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/users/me")
                .then()
                .statusCode(200)
                .extract()
                .path("data.id");

        given()
                .header("Authorization", bearer(userToken))
                .when()
                .get("/users/{id}", userId)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.id", equalTo(userId));
    }

    @Test
    @DisplayName("GET /users/{id}: non-existent ID → 404 Not Found")
    void getUserById_shouldReturn404_whenUserDoesNotExist() {
        given()
                .header("Authorization", bearer(adminToken))
                .when()
                .get("/users/{id}", 999999)
                .then()
                .statusCode(404)
                .body("success", is(false));
    }

    @Test
    @DisplayName("GET /users/{id}: no token → 401 Unauthorized")
    void getUserById_shouldReturn401_whenNotAuthenticated() {
        given()
                .when()
                .get("/users/{id}", 1)
                .then()
                .statusCode(401);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /users/search — ADMIN only
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /users/search: admin → 200 with matching users")
    void searchUsers_shouldReturn200_whenCalledByAdmin() {
        given()
                .header("Authorization", bearer(adminToken))
                .queryParam("keyword", "Admin")
                .when()
                .get("/users/search")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.content", notNullValue());
    }

    @Test
    @DisplayName("GET /users/search: regular user → 403 Forbidden")
    void searchUsers_shouldReturn403_whenCalledByRegularUser() {
        given()
                .header("Authorization", bearer(userToken))
                .queryParam("keyword", "test")
                .when()
                .get("/users/search")
                .then()
                .statusCode(403);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /users/count/active — ADMIN only
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /users/count/active: admin → 200 with count")
    void countActiveUsers_shouldReturn200_whenCalledByAdmin() {
        given()
                .header("Authorization", bearer(adminToken))
                .when()
                .get("/users/count/active")
                .then()
                .statusCode(200)
                .body("data.count", greaterThanOrEqualTo(1));
    }
}
