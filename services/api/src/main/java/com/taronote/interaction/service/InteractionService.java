package com.taronote.interaction.service;

import com.taronote.common.moderation.ModerationService;
import com.taronote.interaction.repository.InteractionRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class InteractionService {
    private final InteractionRepository interactionRepository;
    private final ModerationService moderationService;

    public InteractionService(InteractionRepository interactionRepository, ModerationService moderationService) {
        this.interactionRepository = interactionRepository;
        this.moderationService = moderationService;
    }

    public void like(UUID userId, long noteId) {
        interactionRepository.like(userId, noteId);
    }

    public void collect(UUID userId, long noteId) {
        interactionRepository.collect(userId, noteId);
    }

    public void view(UUID userId, long noteId) {
        interactionRepository.view(userId, noteId);
    }

    public void comment(UUID userId, long noteId, String content) {
        moderationService.assertSafe(content);
        interactionRepository.comment(userId, noteId, content);
    }
}
