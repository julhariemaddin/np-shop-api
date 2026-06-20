package com.ecommerce.np_shop.repo;

import com.ecommerce.np_shop.dto.api.v1.ReviewResponse;
import com.ecommerce.np_shop.entity.Product;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("""
SELECT p
FROM Product p
JOIN p.category c
WHERE
    LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%'))
""")
    Page<Product> search(
            @Param("keyword") String keyword,
            Pageable pageable
    );

}
