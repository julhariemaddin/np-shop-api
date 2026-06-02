package com.ecommerce.np_shop.security;


import com.ecommerce.np_shop.exception.jwtException.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.expiration}")
    private long EXPIRATION;


    public Claims getClaimsFromToken(String token) {
        try {

            return Jwts.parser().verifyWith((SecretKey) getSigningKey())
                    .build().parseSignedClaims(token).getPayload();
        }catch(SecurityException e) {
            throw new NpSignatureException("Invalid token signature", e);

        }
        catch (ExpiredJwtException e) {
            throw new NpExpiredJwtException("Token is expired",e);

        } catch (MalformedJwtException e) {
            throw new NpMalformedJwtException("Token is malformed",e);

        } catch (UnsupportedJwtException e) {
            throw new NpUnsupportedJwtException("Unsupported Token",e);

        } catch (IllegalArgumentException e) {
            throw new NpIllegalArgumentException("Invalid Claims!",e);
        }
    }
    public String generateToken(AccountDetails accountDetails) {
        return Jwts.builder()
                .subject(accountDetails.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }

    public boolean isTokenValid(String token , AccountDetails accountDetails) {

            Claims claims = getClaimsFromToken(token);
            return claims.getSubject().equals(accountDetails.getId().toString()) && !claims.getExpiration().before(new Date());

    }

    public UUID getIdFromToken(String token){
        return UUID.fromString(getClaimsFromToken(token).getSubject());
    }



    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }
}
