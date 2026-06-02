package com.ecommerce.np_shop.exception.jwtException;


import org.springframework.security.core.AuthenticationException;

public class NpMalformedJwtException extends AuthenticationException {

    public NpMalformedJwtException(String msg) {
        super(msg);
    }

    public NpMalformedJwtException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
