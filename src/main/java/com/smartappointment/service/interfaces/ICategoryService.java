package com.smartappointment.service.interfaces;

import java.util.List;

import com.smartappointment.dto.category.requests.CreateCategoryRequest;
import com.smartappointment.dto.category.requests.UpdateCategoryRequest;
import com.smartappointment.dto.category.responses.CategoryResponse;

public interface ICategoryService {

    CategoryResponse createCategory(CreateCategoryRequest request);

    List<CategoryResponse> getAllCategories();

    List<CategoryResponse> getActiveCategories();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse getCategoryByName(String name);

    boolean existsByName(String name);

    CategoryResponse updateCategory(Long id, UpdateCategoryRequest request);

    void softDeleteCategory(Long id);

    void hardDeleteCategory(Long id);
}
