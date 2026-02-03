package com.taronote.content.domain;

import java.time.Instant;

public record Comment(
        long id,
        String userId,
        long noteId,
        String content,
        Instant createdAt
) {
}
