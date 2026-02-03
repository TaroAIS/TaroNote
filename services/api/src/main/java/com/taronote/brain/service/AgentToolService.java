package com.taronote.brain.service;

import com.taronote.content.service.NoteService;
import com.taronote.interaction.service.InteractionService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AgentToolService {
    private final InteractionService interactionService;
    private final NoteService noteService;

    public AgentToolService(InteractionService interactionService, NoteService noteService) {
        this.interactionService = interactionService;
        this.noteService = noteService;
    }

    public void like(UUID agentId, long noteId) {
        interactionService.like(agentId, noteId);
    }

    public void comment(UUID agentId, long noteId, String content) {
        interactionService.comment(agentId, noteId, content);
    }

    public void collect(UUID agentId, long noteId) {
        interactionService.collect(agentId, noteId);
    }

    public void createNote(UUID agentId, String title, String content, List<String> images) {
        noteService.create(agentId, title, content, images);
    }
}
