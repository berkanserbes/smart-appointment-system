package com.smartappointment;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;

/**
 * Base class for all REST Assured integration tests.
 *
 * <ul>
 * <li>Spring Boot starts on a random port; REST Assured is configured to use
 * it.</li>
 * <li>{@link RedisConnectionFactory} is mocked so no real Redis instance is
 * needed.
 * Cache failures are silently swallowed by the custom {@code CacheErrorHandler}
 * in {@code RedisConfig}, so business logic runs normally without caching.</li>
 * <li>H2 (PostgreSQL-mode) replaces the real PostgreSQL database (see test
 * application.yml).</li>
 * <li>Test data isolation is achieved by using UUID-based unique values so
 * tests
 * do not interfere with each other even within the same shared context.</li>
 * </ul>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    /** Maximum acceptable response time for all API calls under test. */
    protected static final long MAX_RESPONSE_TIME_MS = 1000L;
    protected static final TimeUnit RESPONSE_TIME_UNIT = TimeUnit.MILLISECONDS;

    @LocalServerPort
    protected int port;

    /**
     * Mock the Redis connection factory so no real Redis is needed.
     * RedisConfig will build a RedisCacheManager with this mock; cache operations
     * will fail gracefully via the existing CacheErrorHandler.
     */
    @MockBean
    @SuppressWarnings("unused")
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * Mock the mail sender so no real SMTP connection is needed in tests.
     * Any service that sends e-mail (e.g., reminder notifications) will silently
     * use this no-op mock.
     */
    @MockBean
    @SuppressWarnings("unused")
    private JavaMailSender javaMailSender;

    @BeforeEach
    void configureRestAssured() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Auth helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Logs in with the given credentials and returns the JWT token.
     * Token is located at {@code data.token} in the wrapped ApiResponse body.
     */
    protected String loginAndGetToken(String email, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "email": "%s",
                            "password": "%s"
                        }
                        """.formatted(email, password))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("data.token");
    }

    /**
     * Returns a token for the seeded admin user (created by DataInitializer).
     */
    protected String getAdminToken() {
        return loginAndGetToken("admin@smartappointment.com", "Admin123!");
    }

    /**
     * Registers a new user and returns the JWT token obtained from a subsequent
     * login.
     *
     * @param email    unique email (use {@link java.util.UUID#randomUUID()}
     *                 callers)
     * @param password plain-text password
     */
    protected String registerAndGetToken(String firstName, String lastName,
            String email, String password) {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "firstName": "%s",
                            "lastName": "%s",
                            "email": "%s",
                            "password": "%s"
                        }
                        """.formatted(firstName, lastName, email, password))
                .when()
                .post("/auth/register")
                .then()
                .statusCode(201);

        return loginAndGetToken(email, password);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Common request builders
    // ─────────────────────────────────────────────────────────────────────────

    /** Returns the {@code Authorization: Bearer <token>} header value. */
    protected static String bearer(String token) {
        return "Bearer " + token;
    }
}
