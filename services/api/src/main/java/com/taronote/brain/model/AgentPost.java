package com.taronote.brain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AgentPost(
        String title,
        String content,
        @JsonProperty("image_prompt") String imagePrompt
) {
}
