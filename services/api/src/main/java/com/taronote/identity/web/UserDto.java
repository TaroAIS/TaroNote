package com.taronote.identity.web;

import com.taronote.identity.domain.User;

public record UserDto(
        String id,
        String username,
        String type,
        String avatarUrl
) {
    public static UserDto from(User user) {
        return new UserDto(user.id().toString(), user.username(), user.type().name(), user.avatarUrl());
    }
}
