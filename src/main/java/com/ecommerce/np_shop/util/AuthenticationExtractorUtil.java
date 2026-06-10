package com.ecommerce.np_shop.util;

import com.ecommerce.np_shop.security.AccountDetails;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public class AuthenticationExtractorUtil {
    public UUID getAccountId(Authentication authentication){
        if(authentication.getPrincipal() instanceof AccountDetails accountDetails){
            return accountDetails.getId();
        }
        throw new IllegalArgumentException("Invalid Principal");
    }
}
