package com.ecommerce.np_shop.controller.api.v1;

import com.ecommerce.np_shop.service.serviceImpl.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;


import java.net.MalformedURLException;
import java.util.UUID;

@RestController
@RequestMapping("${base.api}")
@RequiredArgsConstructor
public class ImageController {
    private final ProductServiceImpl productService;
    @DeleteMapping("/image/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable(name = "id") UUID imageId) {
       productService.deleteImage(imageId);
        return ResponseEntity.ok("Image deleted successfully");
    }
    @GetMapping("/image/{url}")
    public ResponseEntity<Resource> getImage(@PathVariable String url) throws MalformedURLException {
        String contentType = "application/octet-stream";
    return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(productService.getImageResource(url));
    }
    @PostMapping("/image/{id}")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file , @PathVariable(name = "id") UUID productId){
        return ResponseEntity.ok(productService.addImage(file,productId));
    }
}
