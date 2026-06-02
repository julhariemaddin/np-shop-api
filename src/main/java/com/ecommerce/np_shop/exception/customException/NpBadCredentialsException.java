package com.ecommerce.np_shop.exception.customException;

public class NpBadCredentialsException extends Exception{
    public NpBadCredentialsException(String message) {
        super(message);
    }
    public NpBadCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
