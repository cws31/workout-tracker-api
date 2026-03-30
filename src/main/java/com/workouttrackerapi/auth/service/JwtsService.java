package com.workouttrackerapi.auth.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtsService {

    private String SCRETE = "workoutapiapplicationwhichhelpfitnessenthusiaststomanagetheirworkoutanda=cantrackthirprogress";

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS256, SCRETE)
                .compact();
    }

    public String extracteEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String role(String token) {
        return (String) extractClaims(token).get("role");
    }

    public Boolean validateToken(String email, String token) {
        String extractedEmail = extracteEmail(token);
        return extractedEmail.equals(email) && !isExpired(token);
    }

    public Boolean isExpired(String token) {
        Date expiration = extractClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SCRETE)
                .parseClaimsJws(token)
                .getBody();

    }

}
