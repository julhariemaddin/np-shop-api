package com.ecommerce.np_shop.repo;

import com.ecommerce.np_shop.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
    Optional<Image> findByFileName(String fileName);
}
