package com.ecommerce.np_shop.serviceTest;

import com.ecommerce.np_shop.entity.Category;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CategoryServiceTest {

    public Category getMockCategory() {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setCategoryName("Mock Category");
        return category;
    }
}
