package com.taronote.common.ai;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {
    private static final int DIMENSIONS = 1536;

    public float[] embed(String text) {
        float[] vector = new float[DIMENSIONS];
        if (text == null || text.isBlank()) {
            return vector;
        }
        String normalized = text.toLowerCase(Locale.ROOT);
        String[] tokens = normalized.split("[^a-z0-9\u4e00-\u9fa5]+");
        for (String token : tokens) {
            if (token.isBlank()) {
                continue;
            }
            int hash = murmurHash(token);
            int index = Math.floorMod(hash, DIMENSIONS);
            vector[index] += 1.0f;
        }
        normalize(vector);
        return vector;
    }

    private void normalize(float[] vector) {
        double sum = 0.0;
        for (float v : vector) {
            sum += v * v;
        }
        if (sum == 0.0) {
            return;
        }
        double norm = Math.sqrt(sum);
        for (int i = 0; i < vector.length; i++) {
            vector[i] = (float) (vector[i] / norm);
        }
    }

    private int murmurHash(String input) {
        byte[] data = input.getBytes(StandardCharsets.UTF_8);
        int h1 = 0x9747b28c;
        int length = data.length;
        int i = 0;

        while (length >= 4) {
            int k1 = (data[i] & 0xff)
                    | ((data[i + 1] & 0xff) << 8)
                    | ((data[i + 2] & 0xff) << 16)
                    | (data[i + 3] << 24);
            k1 *= 0xcc9e2d51;
            k1 = Integer.rotateLeft(k1, 15);
            k1 *= 0x1b873593;

            h1 ^= k1;
            h1 = Integer.rotateLeft(h1, 13);
            h1 = h1 * 5 + 0xe6546b64;

            i += 4;
            length -= 4;
        }

        int k1 = 0;
        switch (length) {
            case 3 -> k1 ^= (data[i + 2] & 0xff) << 16;
            case 2 -> k1 ^= (data[i + 1] & 0xff) << 8;
            case 1 -> {
                k1 ^= (data[i] & 0xff);
                k1 *= 0xcc9e2d51;
                k1 = Integer.rotateLeft(k1, 15);
                k1 *= 0x1b873593;
                h1 ^= k1;
            }
            default -> {
            }
        }

        h1 ^= data.length;
        h1 ^= (h1 >>> 16);
        h1 *= 0x85ebca6b;
        h1 ^= (h1 >>> 13);
        h1 *= 0xc2b2ae35;
        h1 ^= (h1 >>> 16);

        return h1;
    }
}
