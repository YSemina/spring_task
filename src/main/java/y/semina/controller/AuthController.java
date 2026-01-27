package y.semina.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import y.semina.dto.AuthResponse;
import y.semina.dto.LoginRequest;
import y.semina.dto.RegistrationRequest;
import y.semina.service.impl.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        log.info("Registration attempt for username: {}", registrationRequest.getUsername());
        AuthResponse response = authService.register(registrationRequest);
        log.info("User registered successfully: {}", registrationRequest.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for username: {}", loginRequest.getUsername());
        AuthResponse response = authService.login(loginRequest);
        log.info("User logged in successfully: {}", loginRequest.getUsername());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/unlock/{username}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> unlockUser(@PathVariable String username) {
        log.info("Unlock account request for user: {} by admin", username);
        authService.unlockUser(username);
        log.info("Account unlocked successfully for user: {}", username);
        return ResponseEntity.ok().build();
    }

}
