package com.taronote.admin.web;

import java.time.Instant;

public record AgentStatus(
        String id,
        String mood,
        int energy,
        String status,
        Instant lastActiveAt
) {
}
