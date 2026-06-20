package com.ecommerce.np_shop.dto.api.v1;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReviewRequest {
    @Min(1)
    @Max(5)
    private int rating;
    @NotBlank
    @Size(min = 20 , max = 100 , message = "should be between 20 and 100")
    private String description;
    @NonNull
    private UUID productId;
}
