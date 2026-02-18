package com.smartappointment.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartappointment.config.swagger.ApiResponseAnnotations.*;
import com.smartappointment.dto.common.PagedResponse;
import com.smartappointment.dto.provider.requests.CreateServiceProviderRequest;
import com.smartappointment.dto.provider.requests.UpdateServiceProviderRequest;
import com.smartappointment.dto.provider.responses.ServiceProviderDetailResponse;
import com.smartappointment.dto.provider.responses.ServiceProviderResponse;
import com.smartappointment.dto.provider.responses.ServiceProviderSummaryResponse;
import com.smartappointment.service.interfaces.IServiceProviderService;
import com.smartappointment.util.PaginationUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/providers")
@Tag(name = "Service Providers", description = "Service provider management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ServiceProviderController {

    private final IServiceProviderService providerService;

    public ServiceProviderController(IServiceProviderService providerService) {
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
    public ResponseEntity<PagedResponse<ServiceProviderSummaryResponse>> getAllProviders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Pageable pageable = PaginationUtils.toPageable(page, pageSize);
        return ResponseEntity.ok(PagedResponse.of(providerService.getAllProviders(pageable), page));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active service providers", description = "Retrieves only active service providers")
    @StandardResponses
    public ResponseEntity<PagedResponse<ServiceProviderSummaryResponse>> getActiveProviders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Pageable pageable = PaginationUtils.toPageable(page, pageSize);
        return ResponseEntity.ok(PagedResponse.of(providerService.getActiveProviders(pageable), page));
    }

    @GetMapping("/inactive")
    @Operation(summary = "Get inactive service providers", description = "Retrieves only inactive service providers")
    @StandardResponses
    public ResponseEntity<PagedResponse<ServiceProviderSummaryResponse>> getInactiveProviders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Pageable pageable = PaginationUtils.toPageable(page, pageSize);
        return ResponseEntity.ok(PagedResponse.of(providerService.getInactiveProviders(pageable), page));
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
    public ResponseEntity<PagedResponse<ServiceProviderSummaryResponse>> getProvidersByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Pageable pageable = PaginationUtils.toPageable(page, pageSize);
        return ResponseEntity.ok(PagedResponse.of(providerService.getProvidersByCategory(categoryId, pageable), page));
    }

    @GetMapping("/category/{categoryId}/active")
    @Operation(summary = "Get active providers by category", description = "Retrieves only active service providers in a specific category")
    @StandardResponses
    public ResponseEntity<PagedResponse<ServiceProviderSummaryResponse>> getActiveProvidersByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Pageable pageable = PaginationUtils.toPageable(page, pageSize);
        return ResponseEntity.ok(PagedResponse.of(providerService.getActiveProvidersByCategory(categoryId, pageable), page));
    }

    @GetMapping("/search")
    @Operation(summary = "Search providers by name", description = "Searches service providers by name keyword (case-insensitive)")
    @StandardResponses
    public ResponseEntity<PagedResponse<ServiceProviderSummaryResponse>> searchProvidersByName(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Pageable pageable = PaginationUtils.toPageable(page, pageSize);
        return ResponseEntity.ok(PagedResponse.of(providerService.searchProvidersByName(keyword, pageable), page));
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
