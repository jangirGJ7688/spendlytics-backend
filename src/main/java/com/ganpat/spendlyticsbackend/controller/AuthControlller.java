package com.ganpat.spendlyticsbackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ganpat.spendlyticsbackend.dto.CreateUserRequest;
import com.ganpat.spendlyticsbackend.dto.LoginRequest;
import com.ganpat.spendlyticsbackend.dto.LoginResponse;
import com.ganpat.spendlyticsbackend.dto.UserResponse;
import com.ganpat.spendlyticsbackend.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
public class AuthControlller {
    
    private final AuthService authService;

    public AuthControlller(AuthService authService) {
        this.authService = authService;
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
    

}
