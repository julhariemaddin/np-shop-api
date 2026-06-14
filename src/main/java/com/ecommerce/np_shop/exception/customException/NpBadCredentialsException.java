package com.ecommerce.np_shop.exception.customException;

public class NpBadCredentialsException extends CustomException{
    public NpBadCredentialsException(String message) {
        super(message);
    }
    public NpBadCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
