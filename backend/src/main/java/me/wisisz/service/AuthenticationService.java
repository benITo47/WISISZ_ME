package me.wisisz.service;

import me.wisisz.exception.AppException.InvalidTokenException;
import me.wisisz.exception.AppException.LoginFailedException;
import me.wisisz.exception.AppException.NotFoundException;
import me.wisisz.exception.AppException.UserAlreadyExistsException;
import me.wisisz.model.Person;
import me.wisisz.model.RefreshToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private PersonService personService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtProvider jwtProvider;

    // private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Boolean register(String emailAddr, String password, String fname, String lname)
            throws UserAlreadyExistsException {
        Optional<Person> existingPerson = personService.getPersonByEmail(emailAddr);
        if (existingPerson.isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        // String hashedPassword =
        // passwordEncoder.encode(registerRequest.getPassword());

        Person person = new Person();
        person.setEmailAddr(emailAddr);
        person.setPasswordHash(password);
        person.setFname(fname);
        person.setLname(lname);

        personService.savePerson(person);
        return true;
    }

    public Map<String, String> login(String emailAddr, String password) throws LoginFailedException {

        Optional<Person> personOptional = personService.getPersonByEmail(emailAddr);

        if (!personOptional.isPresent()) {
            throw new LoginFailedException("Email not registered");
        }

        Person person = personOptional.get();

        /*
         * Password hashing
         * if (!passwordEncoder.matches(password, person.getPasswordHash())) {
         * throw new Exception("Invalid email or password");
         * }
         */

        if (!password.equals(person.getPasswordHash())) {
            throw new LoginFailedException("Invalid email or password");
        }

        String accessToken = jwtProvider.generateAccessToken(person.getId());
        String refreshToken = jwtProvider.generateRefreshToken(person.getId());

        try {
            refreshTokenService.saveRefreshTokenToDatabase(refreshToken, person.getId());
        } catch (Exception e) {
            throw new LoginFailedException("Failed to save Refresh Token");
        }

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    public Boolean logout(String refreshToken) {
        Optional<RefreshToken> tokenOpt = refreshTokenService.getRefreshToken(refreshToken);

        if (tokenOpt.isPresent()) {
            refreshTokenService.deleteRefreshToken(tokenOpt.get());
            return true;
        }

        return false;
    }

    public static record TokenResponse(String accessToken, String refreshToken) {
    }

    public TokenResponse refreshTokens(String refreshToken) throws InvalidTokenException, NotFoundException {
        if (!jwtProvider.isValid(refreshToken)) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }

        Integer personId = Integer.valueOf(jwtProvider.getPersonId(refreshToken));

        Optional<RefreshToken> tokenOpt = refreshTokenService.getRefreshToken(refreshToken);
        if (tokenOpt.isEmpty()) {
            throw new NotFoundException("Refresh token not found in database");
        }

        String newAccessToken = jwtProvider.generateAccessToken(personId);
        String newRefreshToken = jwtProvider.generateRefreshToken(personId);

        refreshTokenService.deleteRefreshToken(tokenOpt.get());
        refreshTokenService.saveRefreshTokenToDatabase(newRefreshToken, personId);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}
