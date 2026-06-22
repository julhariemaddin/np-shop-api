package com.ecommerce.np_shop.exception;


import com.ecommerce.np_shop.exception.customException.NpBadCredentialsException;
import jakarta.servlet.ServletException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String,String> errors =  new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String Key = (error instanceof FieldError fieldError) ? fieldError.getField() : error.getObjectName();
            String message = error.getDefaultMessage();
            errors.put(Key, message);
        });
        return  ResponseEntity.status(HttpStatusCode.valueOf(400)).body(errors);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        return  ResponseEntity.status(HttpStatusCode.valueOf(500)).body(getErrors(e,"500"));
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException e) {
       return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(getErrors(e,"400"));
    }
    @ExceptionHandler(NpBadCredentialsException.class)
    public ResponseEntity<?> handleUserNotFoundException(NpBadCredentialsException e) {
        return ResponseEntity.status(HttpStatusCode.valueOf(401)).body(getErrors(e , "401"));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
    return  ResponseEntity.status(HttpStatusCode.valueOf(400)).body(
            Map.of("Error : ", 400,
            "message : " , "Invalid body request or have unnecessary field"
            )
    );
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public  ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException e) {
        return  ResponseEntity.status(HttpStatusCode.valueOf(404)).body(Map.of(
                "message" , String.format("%s" , e.getMessage())
        ));
    }
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return  ResponseEntity.status(HttpStatusCode.valueOf(415)).body(Map.of(
                "message" , "Unsupported Media Send"
        ));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e ) {
        return  ResponseEntity.status(HttpStatusCode.valueOf(500)).body(getErrors(e,"500"));
    }
    private static Map<String,String> getErrors(Exception e , String code) {
        Map<String,String> errors =  new HashMap<>();
        errors.put("ERROR :","Status %s".formatted(code));
        errors.put("message", e.getMessage());
        return errors;
    }
}

