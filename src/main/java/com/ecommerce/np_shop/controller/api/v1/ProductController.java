package com.ecommerce.np_shop.controller.api.v1;

import com.ecommerce.np_shop.dto.api.v1.ProductRequest;
import com.ecommerce.np_shop.dto.api.v1.ProductResponse;
import com.ecommerce.np_shop.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${base.api}")
@AllArgsConstructor
public class ProductController {
  private final ProductService productService;

  @PostMapping(value = "/product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> addProduct(
      @Valid @RequestPart("requestProduct") ProductRequest product, @RequestPart("file") MultipartFile file) {
    return ResponseEntity.ok(productService.createProduct(product, file));
  }
  @PutMapping(value = "/product/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> updateProduct(
          @Valid @RequestPart("requestProduct") ProductRequest product, @RequestPart(value = "file", required = false) MultipartFile file ,@PathVariable(name = "id") UUID productId) {
    return ResponseEntity.ok(productService.updateProduct(product, file , productId));
  }

  @GetMapping("/product")
  public ResponseEntity<List<ProductResponse>> getAllProducts() {
    return ResponseEntity.ok(productService.getProducts());
  }

  @GetMapping("/product/{id}")
  public ResponseEntity<?> getProduct(@PathVariable(name = "id") UUID productId) {
    return ResponseEntity.ok(productService.getProduct(productId));
  }

  @DeleteMapping("/product/{id}")
  public ResponseEntity<?> deleteProduct(@PathVariable(name = "id") UUID productId) {
    productService.deleteProduct(productId);
    return ResponseEntity.ok().build();
  }
}
