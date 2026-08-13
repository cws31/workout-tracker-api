package com.workouttrackerapi.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.workouttrackerapi.auth.enums.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtsService {

    // Must be at least 256 bits (32 bytes) for HS256
    private final String SECRET = "workoutapiapplicationwhichhelpfitnessenthusiaststomanagetheirworkoutandacantrackthirprogress";

    private Key getSigningKey() {
        byte[] keyBytes = SECRET.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email, Role role) {
        String roleName = (role != null) ? role.name() : "USER";

        return Jwts.builder()
                .setSubject(email)
                .claim("role", roleName) // FIX: Store explicit String (e.g. "USER" or "ROLE_USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extracteEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String role(String token) {
        Object roleObj = extractClaims(token).get("role");
        return roleObj != null ? roleObj.toString() : "USER";
    }

    public Boolean validateToken(String email, String token) {
        try {
            String extractedEmail = extracteEmail(token);
            return (extractedEmail.equals(email) && !isExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean isExpired(String token) {
        Date expiration = extractClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}