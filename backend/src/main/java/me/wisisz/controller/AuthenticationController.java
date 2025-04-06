package me.wisisz.controller;

import me.wisisz.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * /auth/login, POST - Login method to authenticate the user using email and password.
     * 
     * @param loginRequest - Map containing the email ("emailAddrs") and password ("password") from the request body.
     * @return ResponseEntity containing either the JWT tokens ("accessToken", "refreshToken") or an error message.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> postLogin(@RequestBody Map<String, String> loginRequest) {
        try {
            Map<String, String> tokens = authenticationService.postLogin(loginRequest.get("emailAddr"), loginRequest.get("password"));
            return new ResponseEntity<>(tokens, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

}