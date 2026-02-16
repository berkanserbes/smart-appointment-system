package com.smartappointment.service.interfaces;

import java.util.List;

import com.smartappointment.dto.category.CategoryRequest;
import com.smartappointment.dto.category.CategoryResponse;

public interface ICategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    List<CategoryResponse> getAllCategories();

    List<CategoryResponse> getActiveCategories();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse getCategoryByName(String name);

    boolean existsByName(String name);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    void softDeleteCategory(Long id);

    void hardDeleteCategory(Long id);
}
