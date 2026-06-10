package com.ecommerce.np_shop.dto.api.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateProfileResponse{
    private String username;
    private String email;
}
