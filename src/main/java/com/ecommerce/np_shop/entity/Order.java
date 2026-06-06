package com.ecommerce.np_shop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();
    private LocalDateTime createdAt;
    @OneToOne
    private Payment payment;
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
    public double getTotalPrice() {
     return orderItems.stream().mapToDouble(OrderItem::getTotalPrice).sum();
    }
}