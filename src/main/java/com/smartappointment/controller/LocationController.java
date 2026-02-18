package com.smartappointment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartappointment.config.swagger.ApiResponseAnnotations.*;
import com.smartappointment.dto.location.requests.CreateLocationRequest;
import com.smartappointment.dto.location.requests.UpdateLocationRequest;
import com.smartappointment.dto.location.responses.LocationResponse;
import com.smartappointment.service.interfaces.ILocationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/locations")
@Tag(name = "Locations", description = "Location management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class LocationController {

    private final ILocationService locationService;

    public LocationController(ILocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    @Operation(summary = "Create new location", description = "Creates a new location (Admin only)")
    @CreatedResponses
    public ResponseEntity<LocationResponse> createLocation(@Valid @RequestBody CreateLocationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(locationService.createLocation(request));
    }

    @GetMapping
    @Operation(summary = "Get all locations", description = "Retrieves all locations including inactive ones")
    @StandardResponses
    public ResponseEntity<List<LocationResponse>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/active")
    @Operation(summary = "Get active locations", description = "Retrieves only active locations")
    @StandardResponses
    public ResponseEntity<List<LocationResponse>> getActiveLocations() {
        return ResponseEntity.ok(locationService.getActiveLocations());
    }

    @GetMapping("/inactive")
    @Operation(summary = "Get inactive locations", description = "Retrieves only inactive locations")
    @StandardResponses
    public ResponseEntity<List<LocationResponse>> getInactiveLocations() {
        return ResponseEntity.ok(locationService.getInactiveLocations());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get location by ID", description = "Retrieves a specific location by its ID")
    @GetResponses
    public ResponseEntity<LocationResponse> getLocationById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Get locations by city", description = "Retrieves all locations in a specific city")
    @StandardResponses
    public ResponseEntity<List<LocationResponse>> getLocationsByCity(@PathVariable String city) {
        return ResponseEntity.ok(locationService.getLocationsByCity(city));
    }

    @GetMapping("/city/{city}/active")
    @Operation(summary = "Get active locations by city", description = "Retrieves only active locations in a specific city")
    @StandardResponses
    public ResponseEntity<List<LocationResponse>> getActiveLocationsByCity(@PathVariable String city) {
        return ResponseEntity.ok(locationService.getActiveLocationsByCity(city));
    }

    @GetMapping("/search")
    @Operation(summary = "Search locations by name", description = "Searches locations by name keyword (case-insensitive)")
    @StandardResponses
    public ResponseEntity<List<LocationResponse>> searchLocationsByName(@RequestParam String keyword) {
        return ResponseEntity.ok(locationService.searchLocationsByName(keyword));
    }

    @GetMapping("/exists")
    @Operation(summary = "Check if location exists", description = "Checks if a location exists by name and city")
    @StandardResponses
    public ResponseEntity<Boolean> existsByNameAndCity(@RequestParam String name, @RequestParam String city) {
        return ResponseEntity.ok(locationService.existsByNameAndCity(name, city));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update location", description = "Updates an existing location (Admin only)")
    @UpdateResponses
    public ResponseEntity<LocationResponse> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLocationRequest request) {
        return ResponseEntity.ok(locationService.updateLocation(id, request));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate location", description = "Activates an inactive location (Admin only)")
    @DeleteResponses
    public ResponseEntity<Void> activateLocation(@PathVariable Long id) {
        locationService.activateLocation(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/soft")
    @Operation(summary = "Soft delete location", description = "Marks location as inactive without removing from database (Admin only)")
    @DeleteResponses
    public ResponseEntity<Void> softDeleteLocation(@PathVariable Long id) {
        locationService.softDeleteLocation(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard")
    @Operation(summary = "Hard delete location", description = "Permanently removes location from database (Admin only)")
    @DeleteResponses
    public ResponseEntity<Void> hardDeleteLocation(@PathVariable Long id) {
        locationService.hardDeleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
