package me.wisisz.controller;

import me.wisisz.exception.AppException.InvalidTokenException;
import me.wisisz.exception.AppException.LoginFailedException;
import me.wisisz.exception.AppException.NotFoundException;
import me.wisisz.exception.AppException.UserAlreadyExistsException;
import me.wisisz.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private Map<String, String> registerRequest;
    private Map<String, String> loginRequest;
    private Map<String, String> tokens;
    private AuthenticationService.TokenResponse tokenResponse;

    @BeforeEach
    void setUp() {
        // Set up register request
        registerRequest = new HashMap<>();
        registerRequest.put("emailAddr", "test@example.com");
        registerRequest.put("password", "password");
        registerRequest.put("fname", "Test");
        registerRequest.put("lname", "User");

        // Set up login request
        loginRequest = new HashMap<>();
        loginRequest.put("emailAddr", "test@example.com");
        loginRequest.put("password", "password");

        // Set up tokens
        tokens = new HashMap<>();
        tokens.put("accessToken", "access-token");
        tokens.put("refreshToken", "refresh-token");

        // Set up token response
        tokenResponse = new AuthenticationService.TokenResponse("new-access-token", "new-refresh-token");
    }

    @Test
    void register_Success() throws UserAlreadyExistsException, LoginFailedException {
        when(authenticationService.register(anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        when(authenticationService.login(anyString(), anyString())).thenReturn(tokens);

        ResponseEntity<Map<String, String>> response = authenticationController.register(registerRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
        assertEquals("accessToken", response.getBody().keySet().iterator().next());
        assertEquals(tokens.get("accessToken"), response.getBody().get("accessToken"));

        verify(authenticationService).register(registerRequest.get("emailAddr"), registerRequest.get("password"), registerRequest.get("fname"), registerRequest.get("lname"));
        verify(authenticationService).login(registerRequest.get("emailAddr"), registerRequest.get("password"));
    }

    @Test
    void login_Success() throws LoginFailedException {
        when(authenticationService.login(anyString(), anyString())).thenReturn(tokens);

        ResponseEntity<Map<String, String>> response = authenticationController.login(loginRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
        assertEquals("accessToken", response.getBody().keySet().iterator().next());
        assertEquals(tokens.get("accessToken"), response.getBody().get("accessToken"));

        verify(authenticationService).login(loginRequest.get("emailAddr"), loginRequest.get("password"));
    }

    @Test
    void logout_WithRefreshToken() {
        HttpServletResponse response = new MockHttpServletResponse();
        String refreshToken = "refresh-token";
        when(authenticationService.logout(anyString())).thenReturn(true);

        ResponseEntity<Map<String, String>> result = authenticationController.logout(response, refreshToken);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("message", result.getBody().keySet().iterator().next());
        assertEquals("Logged out successfully", result.getBody().get("message"));

        verify(authenticationService).logout(refreshToken);
    }

    @Test
    void logout_WithoutRefreshToken() {
        HttpServletResponse response = new MockHttpServletResponse();

        ResponseEntity<Map<String, String>> result = authenticationController.logout(response, null);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("message", result.getBody().keySet().iterator().next());
        assertEquals("No user was logged in", result.getBody().get("message"));

        verify(authenticationService, never()).logout(anyString());
    }

    @Test
    void refreshTokens_Success() throws InvalidTokenException, NotFoundException {
        String refreshToken = "refresh-token";
        when(authenticationService.refreshTokens(anyString())).thenReturn(tokenResponse);

        ResponseEntity<Map<String, String>> response = authenticationController.refreshTokens(refreshToken);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
        assertEquals("accessToken", response.getBody().keySet().iterator().next());
        assertEquals(tokenResponse.accessToken(), response.getBody().get("accessToken"));

        verify(authenticationService).refreshTokens(refreshToken);
    }

    @Test
    void refreshTokens_WithoutRefreshToken() throws InvalidTokenException, NotFoundException {
        ResponseEntity<Map<String, String>> response = authenticationController.refreshTokens(null);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("message", response.getBody().keySet().iterator().next());
        assertEquals("Refresh token missing", response.getBody().get("message"));

        verify(authenticationService, never()).refreshTokens(anyString());
    }
}