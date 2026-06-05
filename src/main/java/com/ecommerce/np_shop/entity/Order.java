package com.ecommerce.np_shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Long totalPrice;
    private Integer totalItemsQuantity;
    @ManyToOne
    private Account account;
    @OneToMany
    private List<OrderItem> orderItems;
    private LocalDateTime createdAt;
    @OneToOne
    private Payment payment;
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
