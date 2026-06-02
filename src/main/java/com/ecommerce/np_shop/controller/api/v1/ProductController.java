package com.ecommerce.np_shop.controller.api.v1;

import com.ecommerce.np_shop.dto.api.v1.ProductRequest;
import com.ecommerce.np_shop.service.serviceImpl.ProductServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class ProductController {
    private final ProductServiceImpl productService;
    @PostMapping(value = "/product"
    ,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> addProduct(@Valid @RequestPart ProductRequest product , @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(productService.createProduct(product,file));
    }
}
