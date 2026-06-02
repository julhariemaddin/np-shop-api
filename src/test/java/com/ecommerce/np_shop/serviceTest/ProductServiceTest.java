package com.ecommerce.np_shop.serviceTest;

import com.ecommerce.np_shop.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductServiceTest {
    @Autowired
    private CategoryServiceTest categoryServiceTest;
    public Product getMockProduct() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Mock Product");
        product.setDescription("Mock Product");
        product.setImageUrl("Mock Image");
        product.setPrice(123);
        product.setStock(2);
        product.setCategory(categoryServiceTest.getMockCategory());
        return product;
    }
}
