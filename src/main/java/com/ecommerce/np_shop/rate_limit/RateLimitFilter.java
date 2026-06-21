package com.ecommerce.np_shop.rate_limit;

import com.ecommerce.np_shop.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimitService rateLimitService;
    private final JwtService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String IP = request.getRemoteAddr();
        String authorization = request.getHeader("Authorization");
        UUID accountId = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            accountId = jwtService.getIdFromToken(token);
        }
        if(rateLimitService.allowedRequest(accountId , IP , path)){
            filterChain.doFilter(request, response);
        }else {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":429,\"message\":\"Too many requests\"}");
            return;
        }
    }
}
