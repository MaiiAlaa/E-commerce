package org.example.e_commerce.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.encryption.key}")
    private String encryptionKey;

    // Convert the secretKey string to a Key object
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private SecretKeySpec getEncryptionKey() {
        return new SecretKeySpec(encryptionKey.getBytes(), "AES");
    }

    private String encryptRole(String role) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getEncryptionKey());
            byte[] encrypted = cipher.doFinal(role.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting the role", e);
        }
    }

    private String decryptRole(String encryptedRole) {
        if (encryptedRole == null) {
            throw new IllegalArgumentException("The encrypted role cannot be null.");
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, getEncryptionKey());
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedRole));
            System.out.println(original);
            return new String(original);
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting the role", e);
        }
    }


    // Generate JWT token
    public String generateToken(Long userId, String username , String role) {
        Map<String, Object> claims = new HashMap<>();
        String encryptedRole = encryptRole(role);
        claims.put("username", username);
        claims.put("role" , encryptedRole) ;
        return createToken(claims, userId);
    }

    private String createToken(Map<String, Object> claims, Long userId) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours expiration
                .signWith(getSigningKey())
                .compact();
    }

    // Validate JWT token
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // Extract user ID from JWT token
    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract a claim (like username) from JWT token
    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.get("username", String.class));
    }

    // Extract role from JWT token
    public String extractRole(String token) {
        String encryptedRole = extractClaim(token, claims -> claims.get("role", String.class));
        return decryptRole(encryptedRole);
    }


    // Extract a claim from JWT token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("JWT token has expired", e);
        } catch (UnsupportedJwtException e) {
            throw new IllegalArgumentException("Unsupported JWT token", e);
        } catch (MalformedJwtException e) {
            throw new IllegalArgumentException("Malformed JWT token", e);
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}