package com.ecommerce.np_shop.service;

import com.ecommerce.np_shop.dto.api.v1.ProductRequest;
import com.ecommerce.np_shop.dto.api.v1.ProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ProductService {
    public ProductResponse createProduct(ProductRequest productRequest, MultipartFile file);
}
