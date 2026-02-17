package com.smartappointment.config.swagger;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ApiResponseAnnotations {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid request or email already in use"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public @interface RegisterResponses {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid email or password"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public @interface LoginResponses {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operation successful"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public @interface StandardResponses {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Resource successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public @interface CreatedResponses {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resource successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Resource not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public @interface UpdateResponses {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Resource successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Resource not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public @interface DeleteResponses {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resource found"),
        @ApiResponse(responseCode = "404", description = "Resource not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public @interface GetResponses {}
}
