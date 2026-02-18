package com.smartappointment.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.smartappointment.dto.category.requests.CreateCategoryRequest;
import com.smartappointment.dto.category.requests.UpdateCategoryRequest;
import com.smartappointment.dto.category.responses.CategoryResponse;

public interface ICategoryService {

    CategoryResponse createCategory(CreateCategoryRequest request);

    Page<CategoryResponse> getAllCategories(Pageable pageable);

    Page<CategoryResponse> getActiveCategories(Pageable pageable);

    CategoryResponse getCategoryById(Long id);

    CategoryResponse getCategoryByName(String name);

    boolean existsByName(String name);

    CategoryResponse updateCategory(Long id, UpdateCategoryRequest request);

    void softDeleteCategory(Long id);

    void hardDeleteCategory(Long id);
}
