package com.taronote.brain.service;

import com.taronote.brain.model.AgentDecision;
import java.util.concurrent.Semaphore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DecisionEngine {
    private final LlmClient llmClient;
    private final DecisionParser decisionParser;
    private final Semaphore semaphore;

    public DecisionEngine(LlmClient llmClient, DecisionParser decisionParser,
                          @Value("${agent.scheduler.max-llm-concurrency:32}") int maxConcurrency) {
        this.llmClient = llmClient;
        this.decisionParser = decisionParser;
        this.semaphore = new Semaphore(maxConcurrency);
    }

    public AgentDecision decide(String prompt) {
        boolean acquired = false;
        try {
            semaphore.acquire();
            acquired = true;
            String raw = llmClient.complete(prompt, ModelTier.FAST);
            return decisionParser.parse(raw);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalArgumentException("LLM decision interrupted");
        } finally {
            if (acquired) {
                semaphore.release();
            }
        }
    }
}
