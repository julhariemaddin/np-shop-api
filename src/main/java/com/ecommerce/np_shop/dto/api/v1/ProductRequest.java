package com.ecommerce.np_shop.dto.api.v1;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @Min(0)
    private int stock;
    @Min(0)
    private double price;
    @NotNull
    private UUID categoryId;
}
