package com.ecommerce.np_shop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    private UUID productId;
    private String productName;
    private Boolean productStatus;
    private int quantity;
    private double price;

    public double getTotalPrice() {
        return price * quantity;
    }
}
