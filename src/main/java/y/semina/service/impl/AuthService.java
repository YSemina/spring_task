package y.semina.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import y.semina.constant.Role;
import y.semina.dto.AuthResponse;
import y.semina.dto.LoginRequest;
import y.semina.dto.RegistrationRequest;
import y.semina.model.Customer;
import y.semina.model.User;
import y.semina.repository.CustomerRepository;
import y.semina.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 3;

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Transactional
    public AuthResponse register(RegistrationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        User savedUser = userRepository.save(user);

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setContactNumber(Long.parseLong(request.getContactNumber()));
        customer.setUser(savedUser);
        customerRepository.save(customer);

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .username(savedUser.getUsername())
                .role(savedUser.getRole().name())
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Пользователя с таким именем не существует"));

        if (!user.isAccountNonLocked()) {
            log.warn("Login attempt for locked account: {}", request.getUsername());
            throw new LockedException("Учетная запись заблокирована из-за многочисленных неудачных попыток входа в систему.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            if (user.getFailedAttempt() > 0) {
                user.setFailedAttempt(0);
                userRepository.save(user);
                log.info("Failed attempts counter reset for user: {}", request.getUsername());
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtService.generateToken(userDetails);

            return AuthResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .build();
        } catch (BadCredentialsException e) {
            int failedAttempts = user.getFailedAttempt() + 1;
            user.setFailedAttempt(failedAttempts);

            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                user.setAccountNonLocked(false);
                log.warn("Account locked for user: {} after {} failed attempts", request.getUsername(), failedAttempts);
            } else {
                log.warn("Failed login attempt for user: {}. Attempt {}/{}", request.getUsername(), failedAttempts, MAX_FAILED_ATTEMPTS);
            }

            userRepository.save(user);
            throw new BadCredentialsException("Неверный логин или пароль");
        }
    }

    @Transactional
    public void unlockUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));

        if (user.isAccountNonLocked()) {
            log.info("Account unlock requested for already unlocked user: {}", username);
            return;
        }

        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        userRepository.save(user);
        log.info("Account unlocked by admin for user: {}", username);
    }

}
