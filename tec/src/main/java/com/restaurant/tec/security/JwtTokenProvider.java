package com.restaurant.tec.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Proveedor de tokens JWT para autenticación.
 * Genera y valida tokens JWT usando HMAC-SHA con clave secreta.
 * 
 * @author RestaurantTec Team
 * @version 1.0
 */
@Component
public class JwtTokenProvider {

    // En producción, esto debería estar en las variables de entorno
    // Generado con: openssl rand -base64 32
    @Value("${app.jwt-secret:9a4f2c8d3b7a1e6f9g8h7i6j5k4l3m2n1o0p9q8r7s6t5u4v3w2x1y0z}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds:86400000}") // 24 horas
    private long jwtExpirationDate;

    /**
     * Genera un token JWT para un usuario autenticado.
     * 
     * @param authentication objeto de autenticación de Spring Security
     * @return token JWT firmado con expiración de 24 horas
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(key())
                .compact();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parse(token);
            return true;
        } catch (MalformedJwtException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }
}
