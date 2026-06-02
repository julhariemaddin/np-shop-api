package com.ecommerce.np_shop.service.serviceImpl;

import com.ecommerce.np_shop.dto.api.v1.CategoryRequest;
import com.ecommerce.np_shop.dto.api.v1.CategoryResponse;
import com.ecommerce.np_shop.entity.Category;
import com.ecommerce.np_shop.repo.CategoryRepository;
import com.ecommerce.np_shop.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public CategoryResponse createCategory(CategoryRequest createCategoryRequest) {
        Category category = categoryRepository.findByCategoryName(createCategoryRequest.getCategoryName());
        if(category != null){
            throw new RuntimeException(String.format("category name : %s existed", category.getCategoryName()));
        }else {
            category = new Category();
        }
        category.setCategoryName(createCategoryRequest.getCategoryName());
        Category saveCategory = categoryRepository.save(category);
        return new CategoryResponse(saveCategory.getId(),saveCategory.getCategoryName());
    }

    @Override
    public List<CategoryResponse>  getAllCategories() {
       return categoryRepository.findAll().stream()
               .map(
                       c -> new CategoryResponse(c.getId(),c.getCategoryName())
               ).collect(Collectors.toList());
    }

}
