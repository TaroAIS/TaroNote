package com.taronote.brain.service;

import com.taronote.content.port.ContentPort;
import com.taronote.interaction.port.InteractionPort;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AgentToolService {
    private final InteractionPort interactionPort;
    private final ContentPort contentPort;

    public AgentToolService(InteractionPort interactionPort, ContentPort contentPort) {
        this.interactionPort = interactionPort;
        this.contentPort = contentPort;
    }

    public void like(UUID agentId, long noteId) {
        interactionPort.like(agentId, noteId);
    }

    public void comment(UUID agentId, long noteId, String content) {
        interactionPort.comment(agentId, noteId, content);
    }

    public void collect(UUID agentId, long noteId) {
        interactionPort.collect(agentId, noteId);
    }

    public void view(UUID agentId, long noteId) {
        interactionPort.view(agentId, noteId);
    }

    public void createNote(UUID agentId, String title, String content, List<String> images) {
        contentPort.create(agentId, title, content, images, List.of());
    }
}
