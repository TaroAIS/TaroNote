package com.taronote.identity.web;

public record AuthResponse(
        String token,
        UserDto user
) {
}
