package com.ecommerce.np_shop.dto.api.refresh;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RefreshRequest {
    String refreshToken;
}
