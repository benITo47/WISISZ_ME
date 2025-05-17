package me.wisisz.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class JwtProvider {
    private static final String JWT_SECRET_KEY;
    private static final Key KEY;

    static {
        JWT_SECRET_KEY = Dotenv.load().get("JWT_SECRET_KEY");
        KEY = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes());
    }

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 3600000; // 1 hour in milliseconds
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    public String getPersonId(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String generateAccessToken(Integer personId) {
        return Jwts.builder()
                .setSubject(personId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(KEY)
                .compact();
    }

    public String generateRefreshToken(Integer personId) {
        return Jwts.builder()
                .setSubject(personId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(KEY)
                .compact();
    }

    public Boolean isValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
