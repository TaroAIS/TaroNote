package com.taronote.common.ai;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EmbeddingServiceTest {
    @Test
    void embedsTextToUnitVector() {
        EmbeddingService service = new EmbeddingService();
        float[] vector = service.embed("hello world");
        assertThat(vector).hasSize(1536);
        double sum = 0.0;
        for (float v : vector) {
            sum += v * v;
        }
        assertThat(sum).isGreaterThan(0.0);
        assertThat(Math.sqrt(sum)).isBetween(0.99, 1.01);
    }
}
