package me.wisisz.service;

import me.wisisz.model.Person;
import me.wisisz.model.RefreshToken;
import me.wisisz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.jsonwebtoken.JwtException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private PersonService personService;

    @Autowired
    private RefreshTokenService refreshTokenService;





    //private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void postRegister(String emailAddr, String password, String fname, String lname) throws Exception {

        Optional<Person> existingPerson = personService.getPersonByEmail(emailAddr);
        if (existingPerson.isPresent()) {
            throw new Exception("Email is already registered");
        }

        //String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());

        Person person = new Person();
        person.setEmailAddr(emailAddr);
        person.setPasswordHash(password);
        person.setFname(fname);
        person.setLname(lname);

        personService.savePerson(person);

    }

    public Map<String, String> postLogin(String emailAddr, String password) throws Exception {

        Optional<Person> personOptional = personService.getPersonByEmail(emailAddr);

        if (!personOptional.isPresent()) {
            throw new Exception("Email not registered");
        }

        Person person = personOptional.get();

        /* Password hashing
        if (!passwordEncoder.matches(password, person.getPasswordHash())) {
            throw new Exception("Invalid email or password");
        }
        */

        if (!password.equals(person.getPasswordHash())) {
            throw new Exception("Invalid email or password");
        }

        String accessToken = JwtUtil.generateAccessToken(person.getId());
        String refreshToken = JwtUtil.generateRefreshToken(person.getId());

        try{
            refreshTokenService.saveRefreshTokenToDatabase(refreshToken, person.getId());
        } catch (Exception e) {
            throw new Exception("Failed to save Refresh Token");
        }

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        
        return tokens;
    }



    public String postLogoutByAccessToken(String authorizationHeader) throws Exception {
        String token = extractToken(authorizationHeader); // Odcina "Bearer " jeśli trzeba

        Map<String, Object> userInfo = JwtUtil.validateAndParse(token); // Walidacja JWT
        Integer personId = (Integer) userInfo.get("personId");

        Person person = personService.getPersonById(personId)
                .orElseThrow(() -> new Exception("Person not registered"));

        RefreshToken refreshToken = refreshTokenService.getRefreshTokenByPerson(person)
                .orElseThrow(() -> new Exception("No registered tokens for this person"));

        refreshTokenService.deleteRefreshToken(refreshToken);

        return "User logged out using access token";
    }


    public String postLogoutByRefreshToken(String refreshToken) {
        Optional<RefreshToken> tokenOpt = refreshTokenService.getRefreshToken(refreshToken);

        if (tokenOpt.isPresent()) {
            refreshTokenService.deleteRefreshToken(tokenOpt.get());
            System.out.println("[DEBUG] 🗑️ Refresh token deleted.");
        } else {
            System.out.println("[DEBUG] ℹ️ Refresh token not found in DB. Probably already deleted.");
        }

        return "User logged out using refresh token";
    }





    public Map<String, Object> validateToken(String authorizationHeader) throws Exception {
        try {
            String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;

            Map<String, Object> tokens = JwtUtil.validateAndParse(token);
            return tokens;
        } catch (JwtException e) {
            throw new RuntimeException("Invalid or expired token: " + e.getMessage());
        }
    }

    private String extractToken(String header) throws Exception {
        if (header == null || header.isEmpty()) {
            throw new Exception("Authorization header is missing");
        }
        return header.startsWith("Bearer ") ? header.substring(7) : header;
    }

    public Map<String, String> refreshTokens(String refreshToken) throws Exception {
        System.out.println("[DEBUG] 🔄 Start refreshing tokens");
        System.out.println("[DEBUG] Refresh token received: " + refreshToken);

        // 1. Walidacja JWT
        Map<String, Object> claims;
        try {
            claims = validateToken(refreshToken);
            System.out.println("[DEBUG] ✅ Token claims: " + claims);
        } catch (Exception e) {
            System.out.println("[DEBUG] ❌ Token validation failed: " + e.getMessage());
            throw new Exception("Invalid refresh token");
        }

        Integer personId = (Integer) claims.get("personId");
        System.out.println("[DEBUG] 👤 Extracted personId: " + personId);

        // 2. Szukanie w bazie
        Optional<RefreshToken> tokenOpt = refreshTokenService.getRefreshToken(refreshToken);
        if (tokenOpt.isEmpty()) {
            System.out.println("[DEBUG] ❌ Refresh token not found in DB");
            throw new Exception("Refresh token not found in database");
        }
        System.out.println("[DEBUG] ✅ Refresh token found in DB");

        // 3. Generowanie nowych tokenów
        String newAccessToken = JwtUtil.generateAccessToken(personId);
        String newRefreshToken = JwtUtil.generateRefreshToken(personId);
        System.out.println("[DEBUG] 🆕 Generated new accessToken: " + newAccessToken);
        System.out.println("[DEBUG] 🆕 Generated new refreshToken: " + newRefreshToken);

        // 4. Usuwanie starego i zapis nowego
        refreshTokenService.deleteRefreshToken(tokenOpt.get());
        System.out.println("[DEBUG] 🗑️ Old refresh token deleted from DB");

        refreshTokenService.saveRefreshTokenToDatabase(newRefreshToken, personId);
        System.out.println("[DEBUG] 💾 New refresh token saved to DB");

        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        );
    }




}
