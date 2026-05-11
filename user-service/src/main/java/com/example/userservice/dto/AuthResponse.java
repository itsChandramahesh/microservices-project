package com.example.userservice.dto;

public class AuthResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final String message;

    public AuthResponse(Long id, String name, String email, String message) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }
}
