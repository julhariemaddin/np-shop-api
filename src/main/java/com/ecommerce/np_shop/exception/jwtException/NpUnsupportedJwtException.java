package com.ecommerce.np_shop.exception.jwtException;

import org.springframework.security.core.AuthenticationException;

public class NpUnsupportedJwtException extends AuthenticationException {

    public NpUnsupportedJwtException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public NpUnsupportedJwtException(String msg) {
        super(msg);
    }
}
