package me.wisisz.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static final String JWT_SECRET_KEY;
    private static final Key KEY;

    static {
        JWT_SECRET_KEY = Dotenv.load().get("JWT_SECRET_KEY");
        KEY = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes());
    }

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 3600000; // 1 hour in milliseconds
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    private static Claims parseClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static Integer getPersonId(String token) throws Exception {
        try {
            return Integer.valueOf(parseClaims(token).getSubject());
        } catch (JwtException e) {
            throw new RuntimeException("Invalid or expired token: " + e.getMessage());
        }
    }

    public static String generateAccessToken(Integer personId) {
        return Jwts.builder()
                    .setSubject(personId.toString())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                    .signWith(KEY)
                    .compact();
    }

    public static String generateRefreshToken(Integer personId) {
        return Jwts.builder()
                    .setSubject(personId.toString())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                    .signWith(KEY)
                    .compact();
    }

    public static String refreshAccessToken(String refreshToken) throws Exception {
        try {
            Claims claims = parseClaims(refreshToken);
            String personId = claims.getSubject();

            return generateAccessToken(Integer.valueOf(personId));

        } catch (JwtException e) {
            throw new Exception("Invalid or expired refresh token: " + e.getMessage());
        }
    }

    public static Map<String, Object> validateAndParse(String token) throws Exception {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(KEY)
                    .parseClaimsJws(token)
                    .getBody();

            String subject = claims.getSubject();
            if (subject == null) {
                throw new Exception("Invalid token: subject is missing");
            }

            Integer personId = Integer.valueOf(subject);
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("personId", personId);
            return userInfo;

        } catch (ExpiredJwtException e) {
            throw new Exception("Access token expired");
        } catch (JwtException e) {
            throw new Exception("Invalid access token: " + e.getMessage());
        }
    }





}
