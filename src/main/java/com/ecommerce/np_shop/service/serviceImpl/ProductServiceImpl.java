package com.ecommerce.np_shop.service.serviceImpl;

import com.ecommerce.np_shop.dto.api.v1.ProductRequest;
import com.ecommerce.np_shop.dto.api.v1.ProductResponse;
import com.ecommerce.np_shop.entity.Category;
import com.ecommerce.np_shop.entity.Product;
import com.ecommerce.np_shop.repo.CategoryRepository;
import com.ecommerce.np_shop.repo.ProductRepository;
import com.ecommerce.np_shop.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public ProductResponse createProduct(ProductRequest product, MultipartFile file) {
    if (file.isEmpty()) {
      throw new RuntimeException("Image file is empty");
    }
    Category category =
        categoryRepository
            .findById(product.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found"));
    return getProductResponse(createAndSaveProduct(product, file, category));
  }

  @Transactional
  public Product createAndSaveProduct(
      ProductRequest product, MultipartFile file, Category category) {
    Product newProduct = new Product();
    newProduct.setName(product.getName());
    newProduct.setDescription(product.getDescription());
    newProduct.setStock(product.getStock());
    newProduct.setPrice(product.getPrice());
    newProduct.setImageUrl(getImageUrl(file));
    newProduct.setCategory(category);
    return productRepository.save(newProduct);
  }

  public Product checkProductExistsAndGetProduct(UUID productId) {
    return productRepository
        .findById(productId)
        .orElseThrow(() -> new RuntimeException("Product not found"));
  }

  private ProductResponse getProductResponse(Product savedProduct) {
    ProductResponse productResponse = new ProductResponse();
    productResponse.setId(savedProduct.getId());
    productResponse.setName(savedProduct.getName());
    productResponse.setDescription(savedProduct.getDescription());
    productResponse.setStock(savedProduct.getStock());
    productResponse.setPrice(savedProduct.getPrice());
    productResponse.setImageUrl(savedProduct.getImageUrl());
    productResponse.setCategoryId(savedProduct.getCategory().getId());
    return productResponse;
  }

  private static String getImageUrl(MultipartFile file) {
    String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
    Path path = Paths.get("uploads", fileName);
    try {
      Files.createDirectories(path.getParent());
      Files.write(path, file.getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return fileName;
  }
}
