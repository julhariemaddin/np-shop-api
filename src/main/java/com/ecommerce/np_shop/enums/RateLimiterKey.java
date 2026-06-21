package com.ecommerce.np_shop.enums;

import lombok.*;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum RateLimiterKey {
    IP("rl:ip:"),
    ENDPOINT("rl:endpoint:"),
    ACCOUNT("rl:account:"),
    ;
    private String value;

}
