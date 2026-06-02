package com.ecommerce.np_shop.service;

import com.ecommerce.np_shop.dto.api.v1.CategoryRequest;
import com.ecommerce.np_shop.dto.api.v1.CategoryResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    public CategoryResponse createCategory(CategoryRequest createCategoryRequest);
    public List<CategoryResponse> getAllCategories();
}
