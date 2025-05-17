package me.wisisz.controller;

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
	 * /auth/register, POST - Login method to authenticate the user using email and
	 * password.
	 * 
	 * @param registerRequest - Map containing: email ("emailAddr"), password
	 *                        ("password"), firstname ("fname") and lastname
	 *                        ("lname") from the request body.
	 * @return ResponseEntity containing either CREATED status or an error message.
	 */
	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> registerRequest) {
		try {
			authenticationService.register(registerRequest.get("emailAddr"),
					registerRequest.get("password"),
					registerRequest.get("fname"), registerRequest.get("lname"));
			return this.login(registerRequest);
		} catch (Exception e) {
			return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * /auth/login, POST - Login method to authenticate the user using email and
	 * password.
	 * 
	 * @param loginRequest - Map containing the email ("emailAddr") and password
	 *                     ("password") from the request body.
	 * @return ResponseEntity containing either the JWT tokens ("accessToken",
	 *         "refreshToken") or an error message in the header.
	 */
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginRequest) {
		try {
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
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
		}
	}

	/**
	 * /auth/logout, POST - Logout method.
	 * 
	 * @param authorizationHeader - token from the authorization header.
	 * @return ResponseEntity containing either OK status or an error message.
	 */
	@PostMapping("/logout")
	public ResponseEntity<Map<String, String>> logout(HttpServletResponse response,
			@CookieValue(value = "refreshToken", required = false) String refreshToken) {

		if (refreshToken != null) {
			authenticationService.logout(refreshToken);
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

		return ResponseEntity.ok()
				.headers(headers)
				.body(Map.of("message", "Logged out successfully"));
	}

	@PostMapping("/refresh")
	public ResponseEntity<Map<String, String>> refreshTokens(
			@CookieValue(value = "refreshToken", required = false) String refreshToken) {
		if (refreshToken == null) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("message", "Refresh token missing"));
		}

		try {
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
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
		}
	}
}
