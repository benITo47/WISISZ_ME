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

    public String postRegister(String emailAddr, String password, String fname, String lname) throws Exception {

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

        return "Person registered";
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

    public String postLogout(String header) throws Exception {

        Map<String, Object> userInfo = validateToken(header);


        Integer personId = (Integer)userInfo.get("personId");

        Optional<Person> personOptional = personService.getPersonById(personId);

        if (!personOptional.isPresent()) {
            throw new Exception("Person not registered");
        }

        Person person = personOptional.get();

        Optional<RefreshToken> tokenOptional = refreshTokenService.getRefreshTokenByPerson(person);

        if (!tokenOptional.isPresent()) {
            throw new Exception("No registered tokens for this person");
        }

        RefreshToken refreshToken = tokenOptional.get();
        refreshTokenService.deleteRefreshToken(refreshToken);

        return "User logged out";
    }

    public Map<String, Object> validateToken(String authorizationHeader) throws Exception {
        try {
            String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
            Integer personId = JwtUtil.getPersonId(token);
            String newAccessToken = JwtUtil.generateAccessToken(personId);
            String newRefreshToken = JwtUtil.generateRefreshToken(personId);

            refreshTokenService.saveRefreshTokenToDatabase(newRefreshToken, personId);

            Map<String, Object> tokens = new HashMap<>();
            tokens.put("personId", personId);
            tokens.put("accessToken", newAccessToken);
            tokens.put("refreshToken", newRefreshToken);
            return tokens;
        } catch (JwtException e) {
            throw new RuntimeException("Invalid or expired token: " + e.getMessage());
        }
    }

}
