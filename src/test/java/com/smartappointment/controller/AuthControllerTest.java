package com.smartappointment.controller;

import com.smartappointment.BaseIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for {@code POST /api/auth/register} and {@code POST /api/auth/login}.
 *
 * <p>Response body shape (wrapped by ApiResponseAdvice):
 * <pre>
 * {
 *   "success": true,
 *   "statusCode": 200,
 *   "data": { ... },
 *   "error": null
 * }
 * </pre>
 *
 * <p>Test isolation: every test uses a unique UUID-based email so tests are
 * order-independent and safe to run in parallel.
 */
@DisplayName("Auth Controller — /api/auth")
class AuthControllerTest extends BaseIntegrationTest {

    // ─────────────────────────────────────────────────────────────────────────
    // POST /auth/register
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("register: valid request → 201 with user info")
    void register_shouldReturn201_whenRequestIsValid() {
        String email = "register_valid_" + UUID.randomUUID() + "@hotmail.com";

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "firstName": "Ayşe",
                            "lastName": "Kaya",
                            "email": "%s",
                            "password": "Test123!",
                            "phone": "+905551234567"
                        }
                        """.formatted(email))
                .when()
                .post("/auth/register")
                .then()
                .statusCode(201)
                .body("success", is(true))
                .body("data.email", equalTo(email))
                .body("data.role", equalTo("USER"))
                .body("data.fullName", equalTo("Ayşe Kaya"));
    }

    @Test
    @DisplayName("register: missing email → 400 Bad Request")
    void register_shouldReturn400_whenEmailIsMissing() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "firstName": "Test",
                            "lastName": "User",
                            "password": "Test123!"
                        }
                        """)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400)
                .body("success", is(false));
    }

    @Test
    @DisplayName("register: missing first name → 400 Bad Request")
    void register_shouldReturn400_whenFirstNameIsMissing() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "lastName": "User",
                            "email": "missing_fn_%s@hotmail.com",
                            "password": "Test123!"
                        }
                        """.formatted(UUID.randomUUID()))
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400)
                .body("success", is(false));
    }

    @Test
    @DisplayName("register: invalid email format → 400 Bad Request")
    void register_shouldReturn400_whenEmailFormatIsInvalid() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "firstName": "Test",
                            "lastName": "User",
                            "email": "not-an-email",
                            "password": "Test123!"
                        }
                        """)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400)
                .body("success", is(false));
    }

    @Test
    @DisplayName("register: password too short → 400 Bad Request")
    void register_shouldReturn400_whenPasswordIsTooShort() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "firstName": "Test",
                            "lastName": "User",
                            "email": "short_pw_%s@hotmail.com",
                            "password": "12345"
                        }
                        """.formatted(UUID.randomUUID()))
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400)
                .body("success", is(false));
    }

    @Test
    @DisplayName("register: duplicate email → 400 Bad Request")
    void register_shouldReturn409_whenEmailAlreadyExists() {
        String email = "duplicate_" + UUID.randomUUID() + "@hotmail.com";
        String body = """
                {
                    "firstName": "Test",
                    "lastName": "User",
                    "email": "%s",
                    "password": "Test123!"
                }
                """.formatted(email);

        // First registration — must succeed
        given().contentType(ContentType.JSON).body(body)
                .when().post("/auth/register")
                .then().statusCode(201);

        // Second registration with the same email — AuthService throws BadRequestException → 400
        given().contentType(ContentType.JSON).body(body)
                .when().post("/auth/register")
                .then()
                .statusCode(400)
                .body("success", is(false));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /auth/login
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login: valid credentials → 200 with JWT token")
    void login_shouldReturn200WithToken_whenCredentialsAreValid() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "email": "admin@smartappointment.com",
                            "password": "Admin123!"
                        }
                        """)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.token", notNullValue())
                .body("data.email", equalTo("admin@smartappointment.com"))
                .body("data.role", equalTo("ADMIN"))
                .body("data.fullName", notNullValue())
                .body("data.expiresIn", greaterThan(0));
    }

    @Test
    @DisplayName("login: wrong password → 401 Unauthorized")
    void login_shouldReturn401_whenPasswordIsIncorrect() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "email": "admin@smartappointment.com",
                            "password": "wrongPassword!"
                        }
                        """)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401)
                .body("success", is(false));
    }

    @Test
    @DisplayName("login: non-existent user → 401 Unauthorized")
    void login_shouldReturn401_whenUserDoesNotExist() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "email": "ghost_%s@hotmail.com",
                            "password": "Test123!"
                        }
                        """.formatted(UUID.randomUUID()))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401)
                .body("success", is(false));
    }

    @Test
    @DisplayName("login: missing password field → 400 Bad Request")
    void login_shouldReturn400_whenPasswordFieldIsMissing() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "email": "admin@smartappointment.com"
                        }
                        """)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(400)
                .body("success", is(false));
    }

    @Test
    @DisplayName("login: token can be used to access protected resource")
    void login_returnedToken_shouldGrantAccessToProtectedEndpoint() {
        String token = loginAndGetToken("admin@smartappointment.com", "Admin123!");

        given()
                .header("Authorization", bearer(token))
                .when()
                .get("/users/me")
                .then()
                .statusCode(200)
                .body("data.email", equalTo("admin@smartappointment.com"));
    }
}
