package me.wisisz.service;

import me.wisisz.exception.AppException.NotFoundException;
import me.wisisz.model.Person;
import me.wisisz.model.RefreshToken;
import me.wisisz.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private PersonService personService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private Person testPerson;
    private RefreshToken testRefreshToken;
    private List<RefreshToken> refreshTokenList;

    @BeforeEach
    void setUp() {
        // Set up test person
        testPerson = new Person();
        testPerson.setId(1);
        testPerson.setEmailAddr("test@example.com");
        testPerson.setPasswordHash("password");
        testPerson.setFname("Test");
        testPerson.setLname("User");

        // Set up test refresh token
        testRefreshToken = new RefreshToken();
        testRefreshToken.setId(1);
        testRefreshToken.setPerson(testPerson);
        testRefreshToken.setToken("refresh-token");
        testRefreshToken.setIssuedAt(OffsetDateTime.now());
        testRefreshToken.setExpiresAt(OffsetDateTime.now().plusDays(1));
        testRefreshToken.setIsRevoked(false);

        // Set up refresh token list
        refreshTokenList = new ArrayList<>();
        refreshTokenList.add(testRefreshToken);
    }

    @Test
    void getAllRefreshToken_ShouldReturnAllTokens() {
        when(refreshTokenRepository.findAll()).thenReturn(refreshTokenList);

        List<RefreshToken> result = refreshTokenService.getAllRefreshToken();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRefreshToken, result.get(0));
    }

    @Test
    void getRefreshTokenById_ShouldReturnTokenWhenExists() {
        when(refreshTokenRepository.findById(anyInt())).thenReturn(Optional.of(testRefreshToken));

        Optional<RefreshToken> result = refreshTokenService.getRefreshTokenById(1);

        assertTrue(result.isPresent());
        assertEquals(testRefreshToken, result.get());
    }

    @Test
    void getRefreshTokenById_ShouldReturnEmptyWhenNotExists() {
        when(refreshTokenRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<RefreshToken> result = refreshTokenService.getRefreshTokenById(999);

        assertFalse(result.isPresent());
    }

    @Test
    void getRefreshTokenByPerson_ShouldReturnTokenWhenExists() {
        when(refreshTokenRepository.findByPerson(any(Person.class))).thenReturn(Optional.of(testRefreshToken));

        Optional<RefreshToken> result = refreshTokenService.getRefreshTokenByPerson(testPerson);

        assertTrue(result.isPresent());
        assertEquals(testRefreshToken, result.get());
    }

    @Test
    void getRefreshTokenByPerson_ShouldReturnEmptyWhenNotExists() {
        when(refreshTokenRepository.findByPerson(any(Person.class))).thenReturn(Optional.empty());

        Optional<RefreshToken> result = refreshTokenService.getRefreshTokenByPerson(testPerson);

        assertFalse(result.isPresent());
    }

    @Test
    void saveRefreshTokenToDatabase_ShouldUpdateExistingToken() throws NotFoundException {
        when(personService.getPersonById(anyInt())).thenReturn(Optional.of(testPerson));
        when(refreshTokenRepository.findByPerson(any(Person.class))).thenReturn(Optional.of(testRefreshToken));

        refreshTokenService.saveRefreshTokenToDatabase("new-refresh-token", 1);

        assertEquals("new-refresh-token", testRefreshToken.getToken());
        assertFalse(testRefreshToken.getIsRevoked());
        verify(refreshTokenRepository).save(testRefreshToken);
    }

    @Test
    void saveRefreshTokenToDatabase_ShouldCreateNewToken() throws NotFoundException {
        when(personService.getPersonById(anyInt())).thenReturn(Optional.of(testPerson));
        when(refreshTokenRepository.findByPerson(any(Person.class))).thenReturn(Optional.empty());

        refreshTokenService.saveRefreshTokenToDatabase("new-refresh-token", 1);

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void saveRefreshTokenToDatabase_ShouldThrowNotFoundExceptionWhenPersonNotExists() {
        when(personService.getPersonById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> refreshTokenService.saveRefreshTokenToDatabase("refresh-token", 999));
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void saveRefreshToken_ShouldSaveToken() {
        refreshTokenService.saveRefreshToken(testRefreshToken);

        verify(refreshTokenRepository).save(testRefreshToken);
    }

    @Test
    void deleteRefreshToken_ShouldDeleteToken() {
        refreshTokenService.deleteRefreshToken(testRefreshToken);

        verify(refreshTokenRepository).delete(testRefreshToken);
    }

    @Test
    void getRefreshToken_ShouldReturnTokenWhenExists() {
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(testRefreshToken));

        Optional<RefreshToken> result = refreshTokenService.getRefreshToken("refresh-token");

        assertTrue(result.isPresent());
        assertEquals(testRefreshToken, result.get());
    }

    @Test
    void getRefreshToken_ShouldReturnEmptyWhenNotExists() {
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        Optional<RefreshToken> result = refreshTokenService.getRefreshToken("nonexistent-token");

        assertFalse(result.isPresent());
    }
}