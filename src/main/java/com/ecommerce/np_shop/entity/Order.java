package com.ecommerce.np_shop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Integer totalItemsQuantity;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    @OneToMany(mappedBy = "order" , cascade =  CascadeType.ALL , orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems;
    private LocalDateTime createdAt;
    @OneToOne(fetch = FetchType.LAZY , cascade = CascadeType.ALL , orphanRemoval = true)
    private Payment payment;
    private String status;
    private Instant expiredAt;
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
    public double getTotalPrice() {
     return orderItems.stream().mapToDouble(OrderItem::getTotalPrice).sum();
    }
}