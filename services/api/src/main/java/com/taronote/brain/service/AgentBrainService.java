package com.taronote.brain.service;

import com.taronote.brain.model.AgentAction;
import com.taronote.brain.model.AgentDecision;
import com.taronote.common.ai.EmbeddingService;
import com.taronote.content.domain.Note;
import com.taronote.content.service.NoteService;
import com.taronote.brain.repository.AgentMemoryRepository;
import com.taronote.admin.service.AgentAdminService;
import java.util.List;
import java.util.UUID;
import com.taronote.identity.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AgentBrainService {
    private final NoteService noteService;
    private final AgentToolService agentToolService;
    private final DecisionEngine decisionEngine;
    private final EmbeddingService embeddingService;
    private final AgentMemoryRepository agentMemoryRepository;
    private final UserRepository userRepository;
    private final AgentAdminService agentAdminService;

    public AgentBrainService(NoteService noteService,
                             AgentToolService agentToolService,
                             DecisionEngine decisionEngine,
                             EmbeddingService embeddingService,
                             AgentMemoryRepository agentMemoryRepository,
                             UserRepository userRepository,
                             AgentAdminService agentAdminService) {
        this.noteService = noteService;
        this.agentToolService = agentToolService;
        this.decisionEngine = decisionEngine;
        this.embeddingService = embeddingService;
        this.agentMemoryRepository = agentMemoryRepository;
        this.userRepository = userRepository;
        this.agentAdminService = agentAdminService;
    }

    public void run(UUID agentId) {
        List<Note> feed = noteService.fetchFeed(null, 5);
        if (feed.isEmpty()) {
            return;
        }
        userRepository.updateLastActive(agentId);
        Note target = feed.get(0);
        String prompt = buildPrompt(target);
        AgentDecision decision = decisionEngine.decide(prompt);
        act(agentId, target, decision);
        reflect(agentId, target, decision);
    }

    private String buildPrompt(Note note) {
        return "你是TaroNote的AI用户。你看到一条笔记：标题=" + note.title() + ", 内容=" + note.content();
    }

    private void act(UUID agentId, Note target, AgentDecision decision) {
        if (decision == null || decision.action() == null) {
            return;
        }
        AgentAction action = decision.action();
        switch (action) {
            case LIKE -> agentToolService.like(agentId, target.id());
            case COMMENT -> agentToolService.comment(agentId, target.id(), decision.comment() == null ? "很好看" : decision.comment());
            case COLLECT -> agentToolService.collect(agentId, target.id());
            case POST -> {
                if (decision.post() != null) {
                    agentToolService.createNote(agentId, decision.post().title(), decision.post().content(), List.of());
                }
            }
            case VIEW, IGNORE, SEARCH -> {
            }
        }
    }

    private void reflect(UUID agentId, Note target, AgentDecision decision) {
        String memoryText = "我看到了笔记: " + target.title() + ", 行为: " + (decision == null ? "NONE" : decision.action());
        float[] embedding = embeddingService.embed(memoryText);
        agentMemoryRepository.insert(agentId.toString(), memoryText, "REFLECTION", 0.3f, embedding);
        agentAdminService.appendThought(agentId.toString(), memoryText);
    }
}
