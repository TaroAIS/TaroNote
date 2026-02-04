package com.taronote.brain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AgentDecision(
        AgentAction action,
        @JsonProperty("interest_score") int interestScore,
        String reasoning,
        String comment,
        @JsonProperty("search_query") String searchQuery,
        AgentPost post
) {
}
