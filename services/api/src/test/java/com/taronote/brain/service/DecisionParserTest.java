package com.taronote.brain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taronote.brain.model.AgentDecision;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DecisionParserTest {
    @Test
    void parsesSnakeCaseDecision() {
        DecisionParser parser = new DecisionParser(new ObjectMapper());
        String raw = "{\"action\":\"LIKE\",\"interest_score\":7,\"reasoning\":\"ok\",\"comment\":null,\"search_query\":null,\"post\":null}";
        AgentDecision decision = parser.parse(raw);
        assertThat(decision.action().name()).isEqualTo("LIKE");
        assertThat(decision.interestScore()).isEqualTo(7);
    }
}
