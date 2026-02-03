package com.taronote.brain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taronote.brain.model.AgentAction;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class StubLlmClient implements LlmClient {
    private final ObjectMapper objectMapper;

    public StubLlmClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String complete(String prompt, ModelTier tier) {
        try {
            String action = prompt.length() % 3 == 0 ? "COMMENT" : (prompt.length() % 2 == 0 ? "LIKE" : "VIEW");
            Map<String, Object> payload = Map.of(
                    "action", action,
                    "interest_score", Math.min(10, Math.max(1, prompt.length() % 10)),
                    "reasoning", "Auto-generated decision based on prompt length.",
                    "comment", action.equals("COMMENT") ? "看起来很有灵感，收藏了！" : null,
                    "search_query", null,
                    "post", null
            );
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            return "{\"action\":\"" + AgentAction.VIEW + "\",\"interest_score\":5,\"reasoning\":\"fallback\"}";
        }
    }
}
