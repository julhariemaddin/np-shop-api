package com.ecommerce.np_shop.service.serviceImpl;

import com.ecommerce.np_shop.dto.api.v1.CategoryRequest;
import com.ecommerce.np_shop.dto.api.v1.CategoryResponse;
import com.ecommerce.np_shop.entity.Category;
import com.ecommerce.np_shop.repo.CategoryRepository;
import com.ecommerce.np_shop.service.CategoryService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
  private final CategoryRepository categoryRepository;

  @Transactional
  @Override
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
  @CacheEvict(
          value = "products",
          allEntries = true
  )
  public CategoryResponse createCategory(CategoryRequest createCategoryRequest) {
    Category category =
        categoryRepository.findByCategoryName(createCategoryRequest.getCategoryName());
    if (category != null) {
      throw new RuntimeException(
          String.format("category name : %s existed", category.getCategoryName()));
    } else {
      category = new Category();
    }
    category.setCategoryName(createCategoryRequest.getCategoryName());
    Category saveCategory = categoryRepository.save(category);
    return new CategoryResponse(saveCategory.getId(), saveCategory.getCategoryName());
  }

  @Override
  public List<CategoryResponse> getAllCategories() {
    return categoryRepository.findAll().stream()
        .map(c -> new CategoryResponse(c.getId(), c.getCategoryName()))
        .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
  @CacheEvict(
          value = "products",
          allEntries = true
  )
  public void deleteCategory(UUID id) {
    if (!categoryRepository.existsById(id)) {
      throw new RuntimeException(String.format("category : %s not existed", id));
    }
    categoryRepository.deleteById(id);
  }

  @Override
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
  @CacheEvict(
          value = "products",
          allEntries = true
  )
  public CategoryResponse updateCategory(UUID id, CategoryRequest categoryRequest) {
    Category category =
        categoryRepository
            .findById(id)
            .orElseThrow(
                () -> new RuntimeException(String.format("category id : %s not found", id)));
    category.setCategoryName(categoryRequest.getCategoryName());
    Category saveCategory = categoryRepository.save(category);
    return new CategoryResponse(saveCategory.getId(), saveCategory.getCategoryName());
  }

  @Override
  public CategoryResponse getCategoryById(UUID id) {
    Category category =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException(String.format("category : %s not found", id)));
    return new CategoryResponse(category.getId(), category.getCategoryName());
  }
}
