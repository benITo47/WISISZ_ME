package me.wisisz.controller;

import me.wisisz.exception.AppException.InvalidTokenException;
import me.wisisz.exception.AppException.LoginFailedException;
import me.wisisz.exception.AppException.NotFoundException;
import me.wisisz.exception.AppException.UserAlreadyExistsException;
import me.wisisz.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * /auth/register, POST - Register user, then call login handler
     * 
     * @param registerRequest - JSON with "emailAddr", "password", "fname", "lname"
     *
     * @return result of login handler
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> registerRequest)
            throws UserAlreadyExistsException, LoginFailedException {
        authenticationService.register(
                registerRequest.get("emailAddr"),
                registerRequest.get("password"),
                registerRequest.get("fname"),
                registerRequest.get("lname"));
        return this.login(registerRequest);
    }

    /**
     * /auth/login, POST - Login method to authenticate the user using email and
     * password.
     * 
     * @param loginRequest - JSON with "emailAddr", "password"
     *
     * @return ResponseEntity containing the access token ("accessToken")
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginRequest) throws LoginFailedException {
        Map<String, String> tokens = authenticationService.login(loginRequest.get("emailAddr"),
                loginRequest.get("password"));

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.get("refreshToken"))
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(1))
                .sameSite("Lax")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok().headers(headers).body(
                Map.of("accessToken", tokens.get("accessToken")));
    }

    /**
     * /auth/logout, POST - Logout method.
     * 
     * Requires "refreshToken" to be in HTTP only cookies
     *
     * @return ResponseEntity containing "message"
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response,
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null) {
            return ResponseEntity.ok().body(Map.of("message", "No user was logged in"));
        }
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        HttpHeaders headers = new HttpHeaders();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        authenticationService.logout(refreshToken);
        return ResponseEntity.ok()
                .headers(headers)
                .body(Map.of("message", "Logged out successfully"));
    }

    /**
     * /auth/login, POST - update refresh and access tokens
     * 
     * Requires "refreshToken" to be in HTTP only cookies
     *
     * @return ResponseEntity containing the access token ("accessToken")
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshTokens(
            @CookieValue(value = "refreshToken", required = false) String refreshToken)
            throws NotFoundException, InvalidTokenException {
        if (refreshToken == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh token missing"));
        }

        var newTokens = authenticationService.refreshTokens(refreshToken);
        ResponseCookie newRefreshCookie = ResponseCookie.from("refreshToken", newTokens.refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(1))
                .sameSite("Lax")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, newRefreshCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(Map.of("accessToken", newTokens.accessToken()));
    }
}
