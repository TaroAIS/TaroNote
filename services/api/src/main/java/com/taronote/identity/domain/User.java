package com.taronote.identity.domain;

import java.time.Instant;
import java.util.UUID;

public record User(
        UUID id,
        String username,
        String email,
        String passwordHash,
        UserType type,
        String avatarUrl,
        Instant createdAt,
        Instant lastActiveAt
) {
}
