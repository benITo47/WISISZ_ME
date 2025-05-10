package me.wisisz.controller;

import me.wisisz.model.Person;
import me.wisisz.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * /auth/register, POST - Login method to authenticate the user using email and password.
     * 
     * @param registerRequest - Map containing: email ("emailAddr"), password ("password"), firstname ("fname") and lastname ("lname") from the request body.
     * @return ResponseEntity containing either CREATED status or an error message.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> postRegister(@RequestBody Map<String, String> registerRequest) {
        try {
            authenticationService.postRegister(registerRequest.get("emailAddr"), registerRequest.get("password"), registerRequest.get("fname"), registerRequest.get("lname"));
            ResponseEntity<Map<String, String>> response = postLogin(registerRequest);
            return response;

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * /auth/login, POST - Login method to authenticate the user using email and password.
     * 
     * @param loginRequest - Map containing the email ("emailAddr") and password ("password") from the request body.
     * @return ResponseEntity containing either the JWT tokens ("accessToken", "refreshToken") or an error message in the header.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> postLogin(@RequestBody Map<String, String> loginRequest) {
        try {
            Map<String, String> tokens = authenticationService.postLogin(loginRequest.get("emailAddr"), loginRequest.get("password"));

            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.get("refreshToken"))
                    .httpOnly(true)       // Brak dostępu z poziomu JS (ochrona przed XSS)
                    .secure(true)         // Tylko przez HTTPS
                    .path("/")            // Dostępne dla całej domeny
                    .maxAge(Duration.ofDays(1)) // Czas życia ciasteczka (np. 1 dzień)
                    .sameSite("Strict")   // Ochrona przed CSRF (lub "Lax", jeśli masz przekierowania)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
            headers.set("accessToken", tokens.get("accessToken"));


            
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
     * @param authorizationHeader - token from the authorization header.
     * @return ResponseEntity containing either OK status or an error message.
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> postLogout(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {

        try {
            if (authorizationHeader != null) {
                authenticationService.postLogoutByAccessToken(authorizationHeader);
            } else if (refreshToken != null) {
                authenticationService.postLogoutByRefreshToken(refreshToken);
            } else {
                throw new Exception("No token provided for logout");
            }


            ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(0)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, deleteCookie.toString());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(Map.of("message", "Logged out successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshTokens(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {

        try {

            if (refreshToken == null) {
                throw new Exception("No refresh token provided");
            }

            Map<String, String> newTokens = authenticationService.refreshTokens(refreshToken);


            ResponseCookie newRefreshCookie = ResponseCookie.from("refreshToken", newTokens.get("refreshToken"))
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Strict")
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, newRefreshCookie.toString());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(Map.of("accessToken", newTokens.get("accessToken")));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }



}
