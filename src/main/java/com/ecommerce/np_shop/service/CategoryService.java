package com.ecommerce.np_shop.service;

import com.ecommerce.np_shop.dto.api.v1.CategoryRequest;
import com.ecommerce.np_shop.dto.api.v1.CategoryResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface CategoryService {
    public CategoryResponse createCategory(CategoryRequest createCategoryRequest);
    public List<CategoryResponse> getAllCategories();
    public void deleteCategory(UUID id);
    public CategoryResponse updateCategory(UUID id,CategoryRequest updateCategoryRequest);
    public CategoryResponse getCategoryById(UUID id);
}
