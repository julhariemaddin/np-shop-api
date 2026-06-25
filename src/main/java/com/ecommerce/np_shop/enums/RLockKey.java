package com.ecommerce.np_shop.enums;


import lombok.*;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum RLockKey {
    PRODUCT("RLock:product:"),
    ORDER("RLock:order:");
    private String value;


}
