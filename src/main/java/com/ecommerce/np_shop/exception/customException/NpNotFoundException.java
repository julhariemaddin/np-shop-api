package com.ecommerce.np_shop.exception.customException;

public class NpNotFoundException extends RuntimeException{
    public NpNotFoundException(String message) {
        super(message);
    }
    public NpNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
