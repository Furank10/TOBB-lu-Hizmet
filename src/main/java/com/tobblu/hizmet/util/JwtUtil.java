package com.tobblu.hizmet.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import java.util.function.Function;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    // 1. Generate a secure key. In a real app, store this in application.properties!
    // For now, we generate a safe key for testing.
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // 2. This method creates the "Wristband" (Token)
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // Write the username on the wristband
                .setIssuedAt(new Date()) // Write the current time
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Valid for 10 hours
                .signWith(SECRET_KEY) // Sign it so nobody can fake it
                .compact();
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY) // uses the key that signed with
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}