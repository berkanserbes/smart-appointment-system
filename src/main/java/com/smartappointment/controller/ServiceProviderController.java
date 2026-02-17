package com.smartappointment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartappointment.config.swagger.ApiResponseAnnotations.*;
import com.smartappointment.dto.provider.requests.CreateServiceProviderRequest;
import com.smartappointment.dto.provider.requests.UpdateServiceProviderRequest;
import com.smartappointment.dto.provider.responses.ServiceProviderDetailResponse;
import com.smartappointment.dto.provider.responses.ServiceProviderResponse;
import com.smartappointment.dto.provider.responses.ServiceProviderSummaryResponse;
import com.smartappointment.service.impl.ServiceProviderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/providers")
@Tag(name = "Service Providers", description = "Service provider management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ServiceProviderController {

    private final ServiceProviderService providerService;

    public ServiceProviderController(ServiceProviderService providerService) {
        this.providerService = providerService;
    }

    @PostMapping
    @Operation(summary = "Create new service provider", description = "Creates a new service provider (Admin only)")
    @CreatedResponses
    public ResponseEntity<ServiceProviderResponse> createProvider(
            @Valid @RequestBody CreateServiceProviderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(providerService.createProvider(request));
    }

    @GetMapping
    @Operation(summary = "Get all service providers", description = "Retrieves all service providers including inactive ones")
    @StandardResponses
    public ResponseEntity<List<ServiceProviderSummaryResponse>> getAllProviders() {
        return ResponseEntity.ok(providerService.getAllProviders());
    }

    @GetMapping("/active")
    @Operation(summary = "Get active service providers", description = "Retrieves only active service providers")
    @StandardResponses
    public ResponseEntity<List<ServiceProviderSummaryResponse>> getActiveProviders() {
        return ResponseEntity.ok(providerService.getActiveProviders());
    }

    @GetMapping("/inactive")
    @Operation(summary = "Get inactive service providers", description = "Retrieves only inactive service providers")
    @StandardResponses
    public ResponseEntity<List<ServiceProviderSummaryResponse>> getInactiveProviders() {
        return ResponseEntity.ok(providerService.getInactiveProviders());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service provider by ID", description = "Retrieves detailed information about a specific service provider")
    @GetResponses
    public ResponseEntity<ServiceProviderDetailResponse> getProviderById(@PathVariable Long id) {
        return ResponseEntity.ok(providerService.getProviderById(id));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get providers by category", description = "Retrieves all service providers in a specific category")
    @StandardResponses
    public ResponseEntity<List<ServiceProviderSummaryResponse>> getProvidersByCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(providerService.getProvidersByCategory(categoryId));
    }

    @GetMapping("/category/{categoryId}/active")
    @Operation(summary = "Get active providers by category", description = "Retrieves only active service providers in a specific category")
    @StandardResponses
    public ResponseEntity<List<ServiceProviderSummaryResponse>> getActiveProvidersByCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(providerService.getActiveProvidersByCategory(categoryId));
    }

    @GetMapping("/search")
    @Operation(summary = "Search providers by name", description = "Searches service providers by name keyword (case-insensitive)")
    @StandardResponses
    public ResponseEntity<List<ServiceProviderSummaryResponse>> searchProvidersByName(
            @RequestParam String keyword) {
        return ResponseEntity.ok(providerService.searchProvidersByName(keyword));
    }

    @GetMapping("/exists")
    @Operation(summary = "Check if provider exists", description = "Checks if a service provider exists by email")
    @StandardResponses
    public ResponseEntity<Boolean> existsByEmail(@RequestParam String email) {
        return ResponseEntity.ok(providerService.existsByEmail(email));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update service provider", description = "Updates an existing service provider (Admin only)")
    @UpdateResponses
    public ResponseEntity<ServiceProviderResponse> updateProvider(
            @PathVariable Long id,
            @Valid @RequestBody UpdateServiceProviderRequest request) {
        return ResponseEntity.ok(providerService.updateProvider(id, request));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate service provider", description = "Activates an inactive service provider (Admin only)")
    @DeleteResponses
    public ResponseEntity<Void> activateProvider(@PathVariable Long id) {
        providerService.activateProvider(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/soft")
    @Operation(summary = "Soft delete service provider", description = "Marks service provider as inactive without removing from database (Admin only)")
    @DeleteResponses
    public ResponseEntity<Void> softDeleteProvider(@PathVariable Long id) {
        providerService.softDeleteProvider(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard")
    @Operation(summary = "Hard delete service provider", description = "Permanently removes service provider from database (Admin only)")
    @DeleteResponses
    public ResponseEntity<Void> hardDeleteProvider(@PathVariable Long id) {
        providerService.hardDeleteProvider(id);
        return ResponseEntity.noContent().build();
    }
}
