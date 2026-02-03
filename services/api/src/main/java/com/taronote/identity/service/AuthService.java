package com.taronote.identity.service;

import com.taronote.common.security.JwtService;
import com.taronote.common.security.UserPrincipal;
import com.taronote.identity.domain.User;
import com.taronote.identity.domain.UserType;
import com.taronote.identity.repository.UserRepository;
import com.taronote.identity.web.AuthResponse;
import com.taronote.identity.web.LoginRequest;
import com.taronote.identity.web.RegisterRequest;
import com.taronote.identity.web.UserDto;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            throw new IllegalArgumentException("Email already registered");
        });
        UUID id = UUID.randomUUID();
        User user = new User(
                id,
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                UserType.HUMAN,
                null,
                Instant.now(),
                Instant.now()
        );
        userRepository.create(user);
        UserPrincipal principal = new UserPrincipal(user.id(), user.username(), user.type().name());
        String token = jwtService.generateToken(principal);
        return new AuthResponse(token, UserDto.from(user));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        UserPrincipal principal = new UserPrincipal(user.id(), user.username(), user.type().name());
        String token = jwtService.generateToken(principal);
        return new AuthResponse(token, UserDto.from(user));
    }
}
