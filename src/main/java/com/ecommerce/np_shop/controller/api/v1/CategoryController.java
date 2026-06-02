package com.ecommerce.np_shop.controller.api.v1;

import com.ecommerce.np_shop.dto.api.v1.CategoryRequest;
import com.ecommerce.np_shop.dto.api.v1.CategoryResponse;
import com.ecommerce.np_shop.service.serviceImpl.CategoryServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/api/v1")
@RestController
@AllArgsConstructor
public class CategoryController {
  private final CategoryServiceImpl categoryService;

  @PostMapping("/category")
  public ResponseEntity<CategoryResponse> createCategory(
      @Valid @RequestBody CategoryRequest categoryRequest) {
    return ResponseEntity.ok().body(categoryService.createCategory(categoryRequest));
  }

  @GetMapping("/category")
  public ResponseEntity<List<CategoryResponse>> getAllCategories() {
    return ResponseEntity.ok(categoryService.getAllCategories());
  }

  @DeleteMapping("/category/{id}")
  public ResponseEntity<?> deleteCategory(@PathVariable("id") UUID id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.ok(Map.of("message", "Category has been deleted successfully"));
  }

  @PutMapping("/category/{id}")
  public ResponseEntity<?> updateCategory(
      @PathVariable("id") UUID id, @Valid @RequestBody CategoryRequest categoryRequest) {
    return ResponseEntity.ok().body(categoryService.updateCategory(id, categoryRequest));
  }

  @GetMapping("/category/{id}")
  public ResponseEntity<?> getCategoryById(@RequestParam("id") UUID id) {
    return ResponseEntity.ok().body(categoryService.getCategoryById(id));
  }
}
