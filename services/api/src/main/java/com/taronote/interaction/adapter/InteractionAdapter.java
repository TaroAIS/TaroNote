package com.taronote.interaction.adapter;

import com.taronote.interaction.port.InteractionPort;
import com.taronote.interaction.service.InteractionService;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// 互动适配器：把端口调用翻译到当前实现。
@Service
@ConditionalOnProperty(name = "taronote.adapter.mode", havingValue = "default", matchIfMissing = true)
public class InteractionAdapter implements InteractionPort {
    private final InteractionService interactionService;

    public InteractionAdapter(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @Override
    public void like(UUID userId, long noteId) {
        interactionService.like(userId, noteId);
    }

    @Override
    public void collect(UUID userId, long noteId) {
        interactionService.collect(userId, noteId);
    }

    @Override
    public void view(UUID userId, long noteId) {
        interactionService.view(userId, noteId);
    }

    @Override
    public void comment(UUID userId, long noteId, String content) {
        interactionService.comment(userId, noteId, content);
    }
}
