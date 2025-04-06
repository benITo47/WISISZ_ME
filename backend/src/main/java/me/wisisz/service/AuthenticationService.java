package me.wisisz.service;

import me.wisisz.model.Person;
import me.wisisz.util.JwtUtil;
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
    private JwtUtil jwtUtil;

    //private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

        String accessToken = jwtUtil.generateAccessToken(person.getEmailAddr());
        String refreshToken = jwtUtil.generateRefreshToken(person.getEmailAddr());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }
}
