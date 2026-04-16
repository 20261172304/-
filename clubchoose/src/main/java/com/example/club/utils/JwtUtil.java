package com.example.club.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class JwtUtil {
    // 密钥字符串：至少64个字符，仅包含字母数字（避免特殊字符）
    private static final String SECRET_STRING = "mySecretKeyForHS512ThatIsAtLeast512BitsLongThisShouldBeAVeryLongString1234567890";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());
    private static final long EXPIRE = 3600000; // 1小时

    public static String generateToken(Integer userId, String username, String role) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    public static Claims parseToken(String token) {
        try {
            // 如果前端带 "Bearer_" 前缀，去掉
            if (token != null && token.startsWith("Bearer_")) {
                token = token.substring(7);
            }
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims == null ? null : Integer.parseInt(claims.getSubject());
    }

    public static String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims == null ? null : claims.get("username", String.class);
    }
}