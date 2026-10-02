package com.ganpat.spendlyticsbackend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ganpat.spendlyticsbackend.dto.CreateUserRequest;
import com.ganpat.spendlyticsbackend.dto.LoginRequest;
import com.ganpat.spendlyticsbackend.dto.LoginResponse;
import com.ganpat.spendlyticsbackend.dto.UserResponse;
import com.ganpat.spendlyticsbackend.entity.RefreshToken;
import com.ganpat.spendlyticsbackend.entity.User;
import com.ganpat.spendlyticsbackend.exception.ResourceNotFoundException;
import com.ganpat.spendlyticsbackend.repository.ExpenseRepository;
import com.ganpat.spendlyticsbackend.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final ExpenseRepository expenseRepository;

    private final ExpenseCacheService expenseCacheService;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        RefreshTokenService refreshTokenService,
        ExpenseRepository expenseRepository,
        ExpenseCacheService expenseCacheService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.expenseRepository = expenseRepository;
        this.expenseCacheService = expenseCacheService;
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
        
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponse(token, refreshToken.getToken());
    }

    @Transactional
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
        .orElseThrow(() ->
            new ResourceNotFoundException("User not found")
        );

        refreshTokenService.revokeAllUserTokens(userId);

        expenseRepository.deleteAllByUserId(userId);

        expenseCacheService.evictUserSummery(userId);

        userRepository.delete(user);
    }

}
