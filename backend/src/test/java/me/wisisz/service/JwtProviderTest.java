package me.wisisz.service;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtProviderTest {

    @InjectMocks
    private JwtProvider jwtProvider;

    private String accessToken;
    private String refreshToken;
    private Integer testPersonId = 1;

    @BeforeEach
    void setUp() {
        // Generate tokens for testing
        accessToken = jwtProvider.generateAccessToken(testPersonId);
        refreshToken = jwtProvider.generateRefreshToken(testPersonId);
    }

    @Test
    void generateAccessToken_ShouldReturnValidToken() {
        String token = jwtProvider.generateAccessToken(testPersonId);

        assertNotNull(token);
        assertTrue(jwtProvider.isValid(token));
    }

    @Test
    void generateRefreshToken_ShouldReturnValidToken() {
        String token = jwtProvider.generateRefreshToken(testPersonId);

        assertNotNull(token);
        assertTrue(jwtProvider.isValid(token));
    }

    @Test
    void getPersonId_ShouldReturnCorrectId() throws JwtException {
        String personId = jwtProvider.getPersonId(accessToken);

        assertEquals(testPersonId.toString(), personId);
    }

    @Test
    void isValid_ShouldReturnTrueForValidToken() {
        boolean isValid = jwtProvider.isValid(accessToken);

        assertTrue(isValid);
    }

    @Test
    void isValid_ShouldReturnFalseForInvalidToken() {
        boolean isValid = jwtProvider.isValid("invalid.token.string");

        assertFalse(isValid);
    }

    @Test
    void accessTokenAndRefreshToken_ShouldHaveDifferentValues() {
        assertNotEquals(accessToken, refreshToken);
    }
}