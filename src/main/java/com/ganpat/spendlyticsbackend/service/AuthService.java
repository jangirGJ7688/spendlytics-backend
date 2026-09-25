package com.ganpat.spendlyticsbackend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ganpat.spendlyticsbackend.dto.CreateUserRequest;
import com.ganpat.spendlyticsbackend.dto.LoginRequest;
import com.ganpat.spendlyticsbackend.dto.LoginResponse;
import com.ganpat.spendlyticsbackend.dto.UserResponse;
import com.ganpat.spendlyticsbackend.entity.User;
import com.ganpat.spendlyticsbackend.repository.UserRepository;

import jakarta.transaction.Transactional;

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

    @Transactional
    public UserResponse register(CreateUserRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                "Email already registered"
            );
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        user.setPassword(hashedPassword);

        User savedUser = userRepository.save(user);
        return new UserResponse(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail()
        );
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() ->
            new IllegalArgumentException(
                "Invalid email or password"
            )
        );

        boolean passwordMatches = passwordEncoder.matches(
            request.getPassword(),
            user.getPassword()
        );

        if(!passwordMatches) {
            throw new IllegalArgumentException(
                "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        return new LoginResponse(token);
    }

}
