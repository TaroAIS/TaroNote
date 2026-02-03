package com.taronote.brain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taronote.brain.model.AgentDecision;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class DecisionParser {
    private final ObjectMapper objectMapper;

    public DecisionParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AgentDecision parse(String raw) {
        String candidate = sanitize(raw);
        try {
            return objectMapper.readValue(candidate, AgentDecision.class);
        } catch (Exception e) {
            String repaired = repair(candidate);
            try {
                return objectMapper.readValue(repaired, AgentDecision.class);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Failed to parse LLM decision JSON");
            }
        }
    }

    private String sanitize(String raw) {
        if (raw == null) {
            return "{}";
        }
        String trimmed = raw.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceAll("```[a-zA-Z]*", "").replace("```", "").trim();
        }
        return extractJsonObject(trimmed).orElse(trimmed);
    }

    private String repair(String raw) {
        String trimmed = raw.trim();
        if (!trimmed.startsWith("{")) {
            trimmed = "{" + trimmed;
        }
        if (!trimmed.endsWith("}")) {
            trimmed = trimmed + "}";
        }
        return trimmed;
    }

    private Optional<String> extractJsonObject(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return Optional.of(text.substring(start, end + 1));
        }
        return Optional.empty();
    }
}
