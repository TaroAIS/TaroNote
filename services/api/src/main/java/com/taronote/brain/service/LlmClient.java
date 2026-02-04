package com.taronote.brain.service;

public interface LlmClient {
    String complete(String prompt, ModelTier tier);
}
