package com.ecommerce.np_shop.exception.jwtException;


import org.springframework.security.core.AuthenticationException;

public class NpExpiredJwtException extends AuthenticationException {
    public NpExpiredJwtException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public NpExpiredJwtException(String msg) {
        super(msg);
    }
}
