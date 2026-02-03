package com.taronote.content.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Note(
        long id,
        UUID authorId,
        String title,
        String content,
        List<String> images,
        Instant createdAt
) {
}
