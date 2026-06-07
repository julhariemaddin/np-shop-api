package com.ecommerce.np_shop.service.serviceImpl;

import com.ecommerce.np_shop.dto.api.v1.ProductRequest;
import com.ecommerce.np_shop.dto.api.v1.ProductResponse;
import com.ecommerce.np_shop.entity.Category;
import com.ecommerce.np_shop.entity.Product;
import com.ecommerce.np_shop.repo.CategoryRepository;
import com.ecommerce.np_shop.repo.ProductRepository;
import com.ecommerce.np_shop.service.ProductService;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final String UPLOAD_ROOT_PATH = "upload";
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

  @Override
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public ProductResponse updateProduct(
      ProductRequest productRequest, MultipartFile file, UUID productId) {
    Category category =
        categoryRepository
            .findById(productRequest.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found"));
    return getProductResponse(createAndSaveProduct(productRequest, file, category, productId));
  }

  @Override
  public List<ProductResponse> getProducts() {
    return productRepository.findAll().stream().map(this::getProductResponse).toList();
  }

  @Override
  public ProductResponse getProduct(UUID productId) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
    return getProductResponse(product);
  }

  @Override
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public void deleteProduct(UUID productId) {
    Product product = checkProductExistsAndGetProduct(productId);
    removeFile(product.getImageUrl());
    productRepository.deleteById(productId);
  }

  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
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

  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public Product createAndSaveProduct(
      ProductRequest product, MultipartFile file, Category category, UUID productId) {
    Product newProduct =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
    newProduct.setName(product.getName());
    newProduct.setDescription(product.getDescription());
    newProduct.setStock(product.getStock());
    newProduct.setPrice(product.getPrice());
    if (file != null) {
      String imageUrl = newProduct.getImageUrl();
      removeFile(imageUrl);
      newProduct.setImageUrl(getImageUrl(file));
    }
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

  private String getImageUrl(MultipartFile file) {
    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null
        || (!originalFilename.endsWith(".png")
            && !originalFilename.endsWith(".jpg")
            && !originalFilename.endsWith(".jpeg"))) {
      throw new RuntimeException("Only these file format is available .png .jpeg .jpg");
    }
    String fileName = UUID.randomUUID() + "-" + originalFilename;
    Path path = Paths.get(UPLOAD_ROOT_PATH, fileName);
    try {
      Files.createDirectories(path.getParent());
      Files.write(path, file.getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return fileName;
  }
  private void removeFile(String imageUrl){
    Path path = Paths.get(UPLOAD_ROOT_PATH + "/" +Paths.get(imageUrl));
    if (Files.exists(path)) {
      try {
        Files.delete(path);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }
  public boolean checkProductStatus(UUID productId) {
    return  productRepository.existsById(productId);
  }
}
