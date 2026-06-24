package com.ecommerce.np_shop.config;

import com.ecommerce.np_shop.rate_limit.RateLimitFilter;
import com.ecommerce.np_shop.security.JwtAuthenticationEntryPoint;
import com.ecommerce.np_shop.security.JwtAuthenticationFilterChain;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SpringSecurityConfig {
    private final JwtAuthenticationFilterChain jwtAuthenticationFilterChain;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final RateLimitFilter  rateLimitFilter;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.
                csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
                         authorizeRequests
                                 //.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                 .requestMatchers(HttpMethod.GET, "/api/v1/server/check").permitAll()
                                 .requestMatchers(HttpMethod.POST,"/api/v1/product/review/**").hasAnyRole("USER")
                                 .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/category/**").permitAll()
                                 .requestMatchers(HttpMethod.POST,"/api/v1/category/**").hasAnyRole("ADMIN","SUPER_ADMIN")
                                 .requestMatchers(HttpMethod.PUT,"/api/v1/category/**").hasAnyRole("ADMIN","SUPER_ADMIN")
                                 .requestMatchers(HttpMethod.DELETE,"/api/v1/category/**").hasAnyRole("ADMIN","SUPER_ADMIN")
                                 .requestMatchers(HttpMethod.GET,"/api/v1/product/**").permitAll()
                                 .requestMatchers(HttpMethod.POST,"/api/v1/product/**").hasAnyRole("ADMIN","SUPER_ADMIN")
                                 .requestMatchers(HttpMethod.PUT,"/api/v1/product/**").hasAnyRole("ADMIN","SUPER_ADMIN")
                                 .requestMatchers(HttpMethod.DELETE,"/api/v1/product/**").hasAnyRole("ADMIN","SUPER_ADMIN")
                                 .requestMatchers("/api/v1/cart/**").hasRole("USER")
                                 .requestMatchers("/api/v1/order/**").hasRole("USER")
                                 .requestMatchers(HttpMethod.DELETE,"/api/v1/image/**").hasAnyRole("ADMIN","SUPER_ADMIN")
                                 .requestMatchers(HttpMethod.POST,"/api/v1/image/**").hasAnyRole("ADMIN","SUPER_ADMIN")
                                 .requestMatchers("/request/**").permitAll()
                                 .requestMatchers("/api/paypal/**").permitAll()
                                 .requestMatchers("/api/webhook/**").permitAll()
                                 .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilterChain, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(rateLimitFilter, JwtAuthenticationFilterChain.class)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                ;
        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:5173","https://np-shop-web.vercel.app")); // your frontend
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS" ,"PATCH"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}




