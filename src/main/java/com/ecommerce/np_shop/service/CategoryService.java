package com.ecommerce.np_shop.service;

import com.ecommerce.np_shop.dto.api.v1.CategoryRequest;
import com.ecommerce.np_shop.dto.api.v1.CategoryResponse;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface CategoryService {
     CategoryResponse createCategory(CategoryRequest createCategoryRequest);
     List<CategoryResponse> getAllCategories();
     void deleteCategory(UUID id);
     CategoryResponse updateCategory(UUID id,CategoryRequest updateCategoryRequest);
     CategoryResponse getCategoryById(UUID id);
}
