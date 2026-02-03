package com.taronote.common.moderation;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ModerationService {
    private final List<String> banned = List.of("spam", "scam", "hate", "violence");

    public void assertSafe(String text) {
        if (text == null) {
            return;
        }
        String lower = text.toLowerCase();
        for (String word : banned) {
            if (lower.contains(word)) {
                throw new IllegalArgumentException("Content violates moderation rules");
            }
        }
    }
}
