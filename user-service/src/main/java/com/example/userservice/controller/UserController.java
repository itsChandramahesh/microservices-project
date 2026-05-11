package com.example.userservice.controller;

import com.example.userservice.dto.AuthResponse;
import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.SignupRequest;
import com.example.userservice.model.User;
import com.example.userservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/users/signup")
    public AuthResponse signup(@RequestBody SignupRequest signupRequest) {
        return userService.signup(signupRequest);
    }

    @PostMapping("/users/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }
}
