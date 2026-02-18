package com.smartappointment.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartappointment.dto.category.requests.CreateCategoryRequest;
import com.smartappointment.dto.category.requests.UpdateCategoryRequest;
import com.smartappointment.dto.category.responses.CategoryResponse;
import com.smartappointment.exception.BadRequestException;
import com.smartappointment.exception.ResourceNotFoundException;
import com.smartappointment.mapper.CategoryMapper;
import com.smartappointment.model.entity.Category;
import com.smartappointment.repository.CategoryRepository;
import com.smartappointment.service.interfaces.ICategoryService;

@Service
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new BadRequestException("Category already exists: " + request.name());
        }

        Category category = categoryMapper.toEntity(request);

        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    @Override
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toResponse);
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = findCategoryOrThrow(id);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = findCategoryOrThrow(id);
        categoryMapper.updateEntityFromRequest(category, request);

        Category updated = categoryRepository.save(category);
        return categoryMapper.toResponse(updated);
    }

    @Override
    public Page<CategoryResponse> getActiveCategories(Pageable pageable) {
        return categoryRepository.findByActiveTrue(pageable).map(categoryMapper::toResponse);
    }

    @Override
    public CategoryResponse getCategoryByName(String name) {
        Category category = categoryRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + name));
        return categoryMapper.toResponse(category);
    }

    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }

    @Override
    @Transactional
    public void softDeleteCategory(Long id) {
        Category category = findCategoryOrThrow(id);
        category.setActive(false);
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void hardDeleteCategory(Long id) {
        Category category = findCategoryOrThrow(id);
        categoryRepository.delete(category);
    }

    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }
}
