package com.taronote.interaction.adapter;

import com.taronote.common.port.LoggingPort;
import com.taronote.interaction.port.InteractionPort;
import com.taronote.interaction.service.InteractionService;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// 互动适配器：把端口调用翻译到当前实现。
@Service
@ConditionalOnProperty(name = "taronote.adapter.mode", havingValue = "default", matchIfMissing = true)
public class InteractionAdapter implements InteractionPort {
    private final InteractionService interactionService;
    private final LoggingPort loggingPort;

    public InteractionAdapter(InteractionService interactionService, LoggingPort loggingPort) {
        this.interactionService = interactionService;
        this.loggingPort = loggingPort;
    }

    @Override
    public void like(UUID userId, long noteId) {
        interactionService.like(userId, noteId);
        loggingPort.info("interaction.like", basicFields(userId, noteId));
    }

    @Override
    public void collect(UUID userId, long noteId) {
        interactionService.collect(userId, noteId);
        loggingPort.info("interaction.collect", basicFields(userId, noteId));
    }

    @Override
    public void view(UUID userId, long noteId) {
        interactionService.view(userId, noteId);
        loggingPort.info("interaction.view", basicFields(userId, noteId));
    }

    @Override
    public void comment(UUID userId, long noteId, String content) {
        interactionService.comment(userId, noteId, content);
        Map<String, Object> fields = basicFields(userId, noteId);
        fields.put("contentLength", content == null ? 0 : content.length());
        loggingPort.info("interaction.comment", fields);
    }

    private Map<String, Object> basicFields(UUID userId, long noteId) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("userId", userId);
        fields.put("noteId", noteId);
        return fields;
    }
}
