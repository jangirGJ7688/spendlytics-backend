package com.ganpat.spendlyticsbackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ganpat.spendlyticsbackend.dto.CreateUserRequest;
import com.ganpat.spendlyticsbackend.dto.LoginRequest;
import com.ganpat.spendlyticsbackend.dto.LoginResponse;
import com.ganpat.spendlyticsbackend.dto.RefreshTokenRequest;
import com.ganpat.spendlyticsbackend.dto.UserResponse;
import com.ganpat.spendlyticsbackend.entity.RefreshToken;
import com.ganpat.spendlyticsbackend.entity.User;
import com.ganpat.spendlyticsbackend.service.AuthService;
import com.ganpat.spendlyticsbackend.service.JwtService;
import com.ganpat.spendlyticsbackend.service.RefreshTokenService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
public class AuthControlller {
    
    private final AuthService authService;

    private final RefreshTokenService refreshTokenService;

    private final JwtService jwtService;

    public AuthControlller(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
        @Valid @RequestBody CreateUserRequest request) {
        UserResponse userResponse = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity.ok(loginResponse);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
        @RequestBody RefreshTokenRequest request) {

        RefreshToken newRefreshToken = refreshTokenService.
        rotateRefreshToken(request.refreshToken());

        User user = newRefreshToken.getUser();

        String newJwtToken = jwtService.generateToken(user.getId(), user.getEmail());

        return ResponseEntity.ok(
            new LoginResponse(newJwtToken, newRefreshToken.getToken())
        );
    }
    

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @RequestBody RefreshTokenRequest request) {
        
        refreshTokenService.revokeToken(request.refreshToken());
        
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/account")
    public ResponseEntity<Void> deleteAccount(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        authService.deleteAccount(userId);
        return ResponseEntity.noContent().build();
    }
    
}
