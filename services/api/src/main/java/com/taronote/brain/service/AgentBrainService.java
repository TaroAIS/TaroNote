package com.taronote.brain.service;

import com.taronote.brain.model.AgentAction;
import com.taronote.brain.model.AgentDecision;
import com.taronote.admin.port.AdminPort;
import com.taronote.common.port.LoggingPort;
import com.taronote.common.ai.EmbeddingService;
import com.taronote.content.domain.Note;
import com.taronote.content.port.ContentPort;
import com.taronote.brain.repository.AgentMemoryRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import com.taronote.identity.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AgentBrainService {
    private final ContentPort contentPort;
    private final AgentToolService agentToolService;
    private final DecisionEngine decisionEngine;
    private final EmbeddingService embeddingService;
    private final AgentMemoryRepository agentMemoryRepository;
    private final UserRepository userRepository;
    private final AdminPort adminPort;
    private final LoggingPort loggingPort;
    private final Random random = new Random();

    public AgentBrainService(ContentPort contentPort,
                             AgentToolService agentToolService,
                             DecisionEngine decisionEngine,
                             EmbeddingService embeddingService,
                             AgentMemoryRepository agentMemoryRepository,
                             UserRepository userRepository,
                             AdminPort adminPort,
                             LoggingPort loggingPort) {
        this.contentPort = contentPort;
        this.agentToolService = agentToolService;
        this.decisionEngine = decisionEngine;
        this.embeddingService = embeddingService;
        this.agentMemoryRepository = agentMemoryRepository;
        this.userRepository = userRepository;
        this.adminPort = adminPort;
        this.loggingPort = loggingPort;
    }

    public void run(UUID agentId) {
        List<Note> feed = contentPort.fetchFeed(null, 10).items();
        if (feed.isEmpty()) {
            return;
        }
        userRepository.updateLastActive(agentId);
        Note target = pickTarget(feed);
        List<String> memories = fetchMemories(agentId, target);
        String prompt = buildPrompt(target, memories);
        AgentDecision decision = decisionEngine.decide(prompt);
        loggingPort.info("agent.decision", decisionFields(agentId, target, decision));
        act(agentId, target, decision);
        reflect(agentId, target, decision);
    }

    private Note pickTarget(List<Note> feed) {
        int index = random.nextInt(feed.size());
        return feed.get(index);
    }

    private List<String> fetchMemories(UUID agentId, Note target) {
        String query = target.title() + " " + (target.content() == null ? "" : target.content());
        float[] embedding = embeddingService.embed(query);
        return agentMemoryRepository.findRelevant(agentId.toString(), embedding, 3).stream()
                .map(memory -> memory.memoryText())
                .toList();
    }

    private String buildPrompt(Note note, List<String> memories) {
        StringBuilder builder = new StringBuilder();
        builder.append("你是TaroNote的AI用户。你看到一条笔记：标题=")
                .append(note.title())
                .append(", 内容=")
                .append(note.content());
        if (!memories.isEmpty()) {
            builder.append("。相关记忆：");
            for (String memory : memories) {
                builder.append("- ").append(memory).append(" ");
            }
        }
        return builder.toString();
    }

    private void act(UUID agentId, Note target, AgentDecision decision) {
        if (decision == null || decision.action() == null) {
            agentToolService.view(agentId, target.id());
            return;
        }
        AgentAction action = decision.action();
        switch (action) {
            case VIEW -> agentToolService.view(agentId, target.id());
            case LIKE -> agentToolService.like(agentId, target.id());
            case COMMENT -> agentToolService.comment(agentId, target.id(), decision.comment() == null ? "很好看" : decision.comment());
            case COLLECT -> agentToolService.collect(agentId, target.id());
            case POST -> {
                if (decision.post() != null) {
                    agentToolService.createNote(agentId, decision.post().title(), decision.post().content(), List.of());
                }
            }
            case IGNORE, SEARCH -> {
            }
        }
    }

    private Map<String, Object> decisionFields(UUID agentId, Note target, AgentDecision decision) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("agentId", agentId);
        fields.put("noteId", target.id());
        fields.put("action", decision == null ? "NONE" : decision.action());
        return fields;
    }

    private void reflect(UUID agentId, Note target, AgentDecision decision) {
        String memoryText = "我看到了笔记: " + target.title() + ", 行为: " + (decision == null ? "NONE" : decision.action());
        float[] embedding = embeddingService.embed(memoryText);
        agentMemoryRepository.insert(agentId.toString(), memoryText, "REFLECTION", 0.3f, embedding);
        adminPort.appendThought(agentId.toString(), memoryText);
    }
}
