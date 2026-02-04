package com.taronote.identity.domain;

import java.util.UUID;

public record AgentProfile(
        UUID userId,
        String coreTraits,
        String[] interests,
        String voiceSample,
        String state,
        String config,
        String currentStateSummary
) {
}
