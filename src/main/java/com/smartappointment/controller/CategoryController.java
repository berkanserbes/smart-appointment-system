package com.smartappointment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartappointment.config.swagger.ApiResponseAnnotations.*;
import com.smartappointment.dto.category.requests.CreateCategoryRequest;
import com.smartappointment.dto.category.requests.UpdateCategoryRequest;
import com.smartappointment.dto.category.responses.CategoryResponse;
import com.smartappointment.service.impl.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Appointment category management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @Operation(summary = "Create new category", description = "Creates a new appointment category (Admin only)")
    @CreatedResponses
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieves all categories including inactive ones")
    @StandardResponses
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieves a specific category by its ID")
    @GetResponses
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get category by name", description = "Retrieves a category by its name (case-insensitive)")
    @GetResponses
    public ResponseEntity<CategoryResponse> getCategoryByName(@PathVariable String name) {
        return ResponseEntity.ok(categoryService.getCategoryByName(name));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active categories", description = "Retrieves only active categories")
    @StandardResponses
    public ResponseEntity<List<CategoryResponse>> getActiveCategories() {
        return ResponseEntity.ok(categoryService.getActiveCategories());
    }

    @GetMapping("/exists/{name}")
    @Operation(summary = "Check if category exists", description = "Checks if a category with the given name exists")
    @StandardResponses
    public ResponseEntity<Boolean> existsByName(@PathVariable String name) {
        return ResponseEntity.ok(categoryService.existsByName(name));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Updates an existing category (Admin only)")
    @UpdateResponses
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}/soft")
    @Operation(summary = "Soft delete category", description = "Marks category as inactive without removing from database (Admin only)")
    @DeleteResponses
    public ResponseEntity<Void> softDeleteCategory(@PathVariable Long id) {
        categoryService.softDeleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard")
    @Operation(summary = "Hard delete category", description = "Permanently removes category from database (Admin only)")
    @DeleteResponses
    public ResponseEntity<Void> hardDeleteCategory(@PathVariable Long id) {
        categoryService.hardDeleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
