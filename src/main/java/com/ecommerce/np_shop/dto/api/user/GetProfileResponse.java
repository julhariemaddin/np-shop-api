package com.ecommerce.np_shop.dto.api.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetProfileResponse {
    private UUID accountId;
    private String username;
    private String email;
    private List<String> role;
}
