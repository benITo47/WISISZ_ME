package me.wisisz.controller;

import me.wisisz.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * /auth/register, POST - Login method to authenticate the user using email and password.
     * 
     * @param registerRequest - Map containing: email ("emailAddrs"), password ("password"), firstname ("fname") and lastname ("lname") from the request body.
     * @return ResponseEntity containing either CREATED status or an error message.
     */
    @PostMapping("/register")
    public ResponseEntity<String> postRegister(@RequestBody Map<String, String> registerRequest) {
        try {
            String message = authenticationService.postRegister(registerRequest.get("emailAddr"), registerRequest.get("password"), registerRequest.get("fname"), registerRequest.get("lname"));
            return new ResponseEntity<>(message, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * /auth/login, POST - Login method to authenticate the user using email and password.
     * 
     * @param loginRequest - Map containing the email ("emailAddrs") and password ("password") from the request body.
     * @return ResponseEntity containing either the JWT tokens ("accessToken", "refreshToken") or an error message in the header.
     */
    @PostMapping("/login")
    public ResponseEntity<Void> postLogin(@RequestBody Map<String, String> loginRequest) {
        try {
            Map<String, String> tokens = authenticationService.postLogin(loginRequest.get("emailAddr"), loginRequest.get("password"));

            HttpHeaders headers = new HttpHeaders();
            headers.set("accessToken", tokens.get("accessToken"));
            headers.set("refreshToken", tokens.get("refreshToken"));
            
            return new ResponseEntity<>(headers, HttpStatus.OK);
        } catch (Exception e) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Error", e.getMessage());
            return new ResponseEntity<>(headers, HttpStatus.UNAUTHORIZED);
    }
    }

    /**
     * /auth/logout, POST - Logout method.
     * 
     * @param authorizationHeader - token from the authorization header (Bearer).
     * @return ResponseEntity containing either OK status or an error message.
     */
    @PostMapping("/logout")
    public ResponseEntity<String> postLogout(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
            String message = authenticationService.postLogout(token);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

}