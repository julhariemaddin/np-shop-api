package com.ecommerce.np_shop.service;

import com.ecommerce.np_shop.dto.api.v1.ProductRequest;
import com.ecommerce.np_shop.dto.api.v1.ProductResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ProductService {
     ProductResponse createProduct(ProductRequest productRequest, MultipartFile file);
     ProductResponse updateProduct(ProductRequest productRequest, MultipartFile file ,UUID productId);
     List<ProductResponse> getProducts();
     ProductResponse getProduct(UUID productId);
     void deleteProduct(UUID productId);
     boolean checkProductStatus(UUID productId);
}
