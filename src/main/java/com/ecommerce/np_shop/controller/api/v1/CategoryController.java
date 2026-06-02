package com.ecommerce.np_shop.controller.api.v1;


import com.ecommerce.np_shop.dto.api.v1.CategoryRequest;
import com.ecommerce.np_shop.dto.api.v1.CategoryResponse;
import com.ecommerce.np_shop.service.serviceImpl.CategoryServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1")
@RestController
@AllArgsConstructor
public class CategoryController {
    private final CategoryServiceImpl categoryService;

    
    @PostMapping("/category")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok().body(categoryService.createCategory(categoryRequest));
    }

    @GetMapping("/category")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}
