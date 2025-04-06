package me.wisisz.service;

import me.wisisz.model.Person;
import me.wisisz.model.RefreshToken;
import me.wisisz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.OffsetDateTime;
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
    private JwtUtil jwtUtil;

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

        Optional<RefreshToken> refreshTokenOptional = refreshTokenService.getRefreshTokenByPerson(person);

        if (refreshTokenOptional.isPresent()) {
            throw new Exception("Refresh Token already exists");
        }

        String accessToken = jwtUtil.generateAccessToken(person.getEmailAddr());
        String refreshToken = jwtUtil.generateRefreshToken(person.getEmailAddr());

        RefreshToken modelToken = new RefreshToken();
        modelToken.setPerson(person);
        modelToken.setToken(refreshToken);
        modelToken.setIssuedAt(OffsetDateTime.now());
        modelToken.setExpiresAt(OffsetDateTime.now().plusDays(1));
        modelToken.setIsRevoked(false);

        refreshTokenService.saveRefreshToken(modelToken);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    public String postLogout(String accessToken) throws Exception {

        jwtUtil.validateToken(accessToken);
        String emailAddr = jwtUtil.getEmail(accessToken);

        Optional<Person> personOptional = personService.getPersonByEmail(emailAddr);

        if (!personOptional.isPresent()) {
            throw new Exception("Email not registered");
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
}
