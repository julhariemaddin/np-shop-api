package com.ecommerce.np_shop.serviceTest;

import com.ecommerce.np_shop.dto.api.v1.CategoryRequest;
import com.ecommerce.np_shop.dto.api.v1.CategoryResponse;
import com.ecommerce.np_shop.entity.Category;
import com.ecommerce.np_shop.repo.CategoryRepository;
import com.ecommerce.np_shop.service.serviceImpl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Service
@SpringBootTest
public class CategoryServiceTest {
  @Mock private CategoryRepository categoryRepository;
  @InjectMocks private CategoryServiceImpl categoryService;
  private Category category, savedCategory;
  private UUID id;
  private final List<Category> categories = new ArrayList<>();
  private CategoryRequest categoryRequest;

  @BeforeEach
  public void setup() {
    id = UUID.randomUUID();
    category = getCategory();
    for (int i = 0; i < 10; i++) {
      categories.add(getCategory());
    }
    categoryRequest = new CategoryRequest();
    categoryRequest.setCategoryName("test");
    savedCategory = new Category();
    savedCategory.setCategoryName(categoryRequest.getCategoryName());
    savedCategory.setId(id);
  }

  @Test
  void getAllCategories_ShouldReturnAllCategories_WhenCategoryExist() {
    when(categoryRepository.findAll()).thenReturn(categories);
    List<CategoryResponse> categoriesResponse = categoryService.getAllCategories();
    assertNotNull(categoriesResponse);
  }

  @Test
  void getAllCategories_ShouldReturnAnEmptyList_WhenTheresNoCategoryExist() {
    when(categoryRepository.findAll()).thenReturn(List.of());
    List<CategoryResponse> categoriesResponse = categoryService.getAllCategories();
    assert (categoriesResponse.isEmpty());
  }

  @Test
  void getCategoryById_ShouldReturnCategory_WhenCategoryExists() {
    when(categoryRepository.findById(id)).thenReturn(Optional.ofNullable(category));
    CategoryResponse result = categoryService.getCategoryById(id);
    assertNotNull(result);
  }

  @Test
  void getCategoryById_ShouldThrowAnException_WhenCategoryDoesNotExist() {
    when(categoryRepository.findById(id)).thenReturn(null);
    assertThrows(RuntimeException.class, () -> categoryService.getCategoryById(id));
  }

  @Test
  void updateCategory_ShouldUpdateCategory_WhenCategoryExists() {
    when(categoryRepository.findById(id)).thenReturn(Optional.ofNullable(category));
    when(categoryRepository.save(category)).thenReturn(savedCategory);
    CategoryResponse result = categoryService.updateCategory(id, categoryRequest);
    assertNotNull(result);
    assertEquals(categoryRequest.getCategoryName(), result.getCategoryName());
  }

  @Test
  void updateCategory_ShouldThrowAnException_WhenCategoryDoesNotExist() {
    when(categoryRepository.findById(id)).thenReturn(null);
    assertThrows(RuntimeException.class, () -> categoryService.updateCategory(id, categoryRequest));
  }

  @Test
  void deleteCategory_ShouldDeleteCategory_WhenCategoryExists() {
    when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
    when(categoryRepository.existsById(category.getId())).thenReturn(true);
    categoryService.deleteCategory(category.getId());
    verify(categoryRepository, times(1)).deleteById(category.getId());
  }

  @Test
  void deleteCategory_ShouldThrowsAnException_WhenCategoryDoesNotExists() {
    when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
    when(categoryRepository.existsById(category.getId())).thenReturn(false);
    assertThrows(RuntimeException.class, () -> categoryService.deleteCategory(category.getId()));
  }

  private Category getCategory() {
    return new Category(UUID.randomUUID(), getRandomString());
  }

  private String getRandomString() {
    return UUID.randomUUID().toString();
  }
}
