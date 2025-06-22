package me.wisisz.service;

import me.wisisz.exception.AppException.InvalidTokenException;
import me.wisisz.exception.AppException.LoginFailedException;
import me.wisisz.exception.AppException.NotFoundException;
import me.wisisz.exception.AppException.UserAlreadyExistsException;
import me.wisisz.model.Person;
import me.wisisz.model.RefreshToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private PersonService personService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthenticationService authenticationService;

    private Person testPerson;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        testPerson = new Person();
        testPerson.setId(1);
        testPerson.setEmailAddr("test@example.com");
        testPerson.setPasswordHash("password");
        testPerson.setFname("Test");
        testPerson.setLname("User");

        testRefreshToken = new RefreshToken();
        testRefreshToken.setToken("refresh-token");
        testRefreshToken.setPerson(testPerson);
    }

    @Test
    void register_Success() throws UserAlreadyExistsException {
        when(personService.getPersonByEmail(anyString())).thenReturn(Optional.empty());

        Boolean result = authenticationService.register("test@example.com", "password", "Test", "User");

        assertTrue(result);
        verify(personService).savePerson(any(Person.class));
    }

    @Test
    void register_UserAlreadyExists() {
        when(personService.getPersonByEmail(anyString())).thenReturn(Optional.of(testPerson));

        assertThrows(UserAlreadyExistsException.class, () -> authenticationService.register("test@example.com", "password", "Test", "User"));
        verify(personService, never()).savePerson(any(Person.class));
    }

    @Test
    void login_Success() throws LoginFailedException, NotFoundException {
        when(personService.getPersonByEmail(anyString())).thenReturn(Optional.of(testPerson));
        when(jwtProvider.generateAccessToken(anyInt())).thenReturn("access-token");
        when(jwtProvider.generateRefreshToken(anyInt())).thenReturn("refresh-token");

        Map<String, String> tokens = authenticationService.login("test@example.com", "password");

        assertNotNull(tokens);
        assertEquals("access-token", tokens.get("accessToken"));
        assertEquals("refresh-token", tokens.get("refreshToken"));
        verify(refreshTokenService).saveRefreshTokenToDatabase(anyString(), anyInt());
    }

    @Test
    void login_UserNotFound() {
        when(personService.getPersonByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(LoginFailedException.class, () -> authenticationService.login("nonexistent@example.com", "password"));
        verify(jwtProvider, never()).generateAccessToken(anyInt());
        verify(jwtProvider, never()).generateRefreshToken(anyInt());
    }

    @Test
    void login_InvalidPassword() {
        when(personService.getPersonByEmail(anyString())).thenReturn(Optional.of(testPerson));

        assertThrows(LoginFailedException.class, () -> authenticationService.login("test@example.com", "wrong-password"));
        verify(jwtProvider, never()).generateAccessToken(anyInt());
        verify(jwtProvider, never()).generateRefreshToken(anyInt());
    }

    @Test
    void logout_Success() {
        when(refreshTokenService.getRefreshToken(anyString())).thenReturn(Optional.of(testRefreshToken));

        Boolean result = authenticationService.logout("refresh-token");

        assertTrue(result);
        verify(refreshTokenService).deleteRefreshToken(any(RefreshToken.class));
    }

    @Test
    void logout_TokenNotFound() {
        when(refreshTokenService.getRefreshToken(anyString())).thenReturn(Optional.empty());

        Boolean result = authenticationService.logout("nonexistent-token");

        assertFalse(result);
        verify(refreshTokenService, never()).deleteRefreshToken(any(RefreshToken.class));
    }

    @Test
    void refreshTokens_Success() throws InvalidTokenException, NotFoundException {
        when(jwtProvider.isValid(anyString())).thenReturn(true);
        when(jwtProvider.getPersonId(anyString())).thenReturn("1");
        when(refreshTokenService.getRefreshToken(anyString())).thenReturn(Optional.of(testRefreshToken));
        when(jwtProvider.generateAccessToken(anyInt())).thenReturn("new-access-token");
        when(jwtProvider.generateRefreshToken(anyInt())).thenReturn("new-refresh-token");

        AuthenticationService.TokenResponse response = authenticationService.refreshTokens("refresh-token");

        assertNotNull(response);
        assertEquals("new-access-token", response.accessToken());
        assertEquals("new-refresh-token", response.refreshToken());
        verify(refreshTokenService).deleteRefreshToken(any(RefreshToken.class));
        verify(refreshTokenService).saveRefreshTokenToDatabase(anyString(), anyInt());
    }

    @Test
    void refreshTokens_InvalidToken() {
        when(jwtProvider.isValid(anyString())).thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> authenticationService.refreshTokens("invalid-token"));
        verify(refreshTokenService, never()).getRefreshToken(anyString());
    }

    @Test
    void refreshTokens_TokenNotFound() {
        when(jwtProvider.isValid(anyString())).thenReturn(true);
        when(jwtProvider.getPersonId(anyString())).thenReturn("1");
        when(refreshTokenService.getRefreshToken(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authenticationService.refreshTokens("nonexistent-token"));
        verify(jwtProvider, never()).generateAccessToken(anyInt());
        verify(jwtProvider, never()).generateRefreshToken(anyInt());
    }
}
