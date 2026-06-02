package com.ecommerce.np_shop.exception.jwtException;

import org.springframework.security.core.AuthenticationException;

public class NpSignatureException extends AuthenticationException {
    public NpSignatureException(String msg, Throwable cause) {
        super(msg, cause);
    }
    public NpSignatureException(String msg) {
        super(msg);
    }
}
