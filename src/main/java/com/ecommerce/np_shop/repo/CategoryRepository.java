package com.ecommerce.np_shop.repo;

import com.ecommerce.np_shop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID>{
    Category findByCategoryName(String categoryName);
}
