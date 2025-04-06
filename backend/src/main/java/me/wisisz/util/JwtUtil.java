package me.wisisz.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private static final String JWT_SECRET_KEY = Dotenv.load().get("JWT_SECRET_KEY");
    private static final Key KEY = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes());

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 3600000; // 1 hour in milliseconds
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    public String generateAccessToken(String emailAddr) {
        return Jwts.builder()
                    .setSubject(emailAddr)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                    .signWith(KEY)
                    .compact();
    }

    public String generateRefreshToken(String emailAddr) {
        return Jwts.builder()
                    .setSubject(emailAddr)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                    .signWith(KEY)
                    .compact();
    }

    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token);
        } catch (JwtException e) {
            throw new RuntimeException("Invalid or expired token: " + e.getMessage());
        }
    }

    public String refreshAccessToken(String refreshToken) throws Exception {
        try {
            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(KEY)
                                .build()
                                .parseClaimsJws(refreshToken)
                                .getBody();

            String emailAddr = claims.getSubject();
            Date expiration = claims.getExpiration();

            if (expiration.before(new Date())) {
                throw new Exception("Refresh token expired");
            }

            return generateAccessToken(emailAddr);

        } catch (JwtException e) {
            throw new Exception("Invalid refresh token: " + e.getMessage());
        }
    }

    public String getEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
