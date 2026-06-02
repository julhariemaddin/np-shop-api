package com.ecommerce.np_shop.exception.jwtException;

import org.springframework.security.core.AuthenticationException;

public class NpIllegalArgumentException extends AuthenticationException {
    public NpIllegalArgumentException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public NpIllegalArgumentException(String msg) {
        super(msg);
    }
}
