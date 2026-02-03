package com.taronote.brain.model;

import java.time.Instant;

public record AgentMemory(
        long id,
        String agentId,
        String memoryText,
        String type,
        float importance,
        Instant recency
) {
}
