package com.ecommerce.np_shop.entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NonNull
    private String name;
    @NonNull
    private String description;
    private int stock;
    private int reserveStock;
    private double price;
    private LocalDateTime createdAt;
    private double overAllRating;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @OneToMany(mappedBy = "product" , cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Image> images = new ArrayList<>();
    private String mainImageUrl;
    @OneToMany(mappedBy = "product" , cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
    public void addImage(Image image) {
        images.add(image);
        image.setProduct(this);
    }
    public int getStock(){
        return stock - reserveStock;
    }


    @PrePersist
    protected void prePersist(){
        createTimestamp();
        calculateOverAllRating();
    }

    protected void createTimestamp(){
        this.createdAt = LocalDateTime.now();
    }
    public void calculateOverAllRating(){
        int totalReviewValue = reviews.stream().mapToInt(Review::getRating).sum();
        double overAllRating = (double) totalReviewValue / reviews.size();
        this.overAllRating = Math.round(overAllRating * 10.0) / 10.0;
    }


}
