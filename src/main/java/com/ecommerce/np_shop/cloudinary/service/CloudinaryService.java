package com.ecommerce.np_shop.cloudinary.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;
    @Transactional
    @SuppressWarnings("unchecked")
    public Map<String , Object> uploadImage(MultipartFile file) throws IOException {
        return cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder" , "products",
                        "resource_type" , "image"
                )
        );
    }
    @Transactional
    public void deleteImage(String publicId){
        try{
       cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.emptyMap()
        );
        }catch(Exception e){
            throw new RuntimeException("Error deleting image , "+e.getMessage());
        }
    }
}
