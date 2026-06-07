package com.ecommerce.np_shop.security;

import com.ecommerce.np_shop.exception.GlobalExceptionHandler;
import com.ecommerce.np_shop.exception.jwtException.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String message;
        String errorCode;

        switch (authException) {
            case NpExpiredJwtException npExpiredJwtException -> {
                errorCode = "TOKEN_EXPIRED";
                message = authException.getMessage();
            }
            case NpSignatureException npSignatureException -> {
                errorCode = "INVALID_SIGNATURE";
                message = authException.getMessage();
            }
            case NpMalformedJwtException npMalformedJwtException -> {
                errorCode = "MALFORMED_TOKEN";
                message = authException.getMessage();
            }
            case NpUnsupportedJwtException npUnsupportedJwtException -> {
                errorCode = "UNSUPPORTED_TOKEN";
                message = authException.getMessage();
            }
            case NpIllegalArgumentException npIllegalArgumentException -> {
                errorCode = "INVALID_CLAIMS";
                message = authException.getMessage();
            }
            case null, default -> {
                errorCode = "UNAUTHORIZED";
                message = "Authentication failed or Unauthorized access";
            }
        }

        response.getWriter().write("""
            {
              "status": 401,
              "error": "%s",
              "message": "%s"
            }
            """.formatted(errorCode, message));
    }
}
