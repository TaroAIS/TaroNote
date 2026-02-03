package com.taronote.admin.web;

import com.taronote.admin.service.AgentAdminService;
import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/admin/agents/thoughts")
public class ThoughtStreamController {
    private final AgentAdminService agentAdminService;

    public ThoughtStreamController(AgentAdminService agentAdminService) {
        this.agentAdminService = agentAdminService;
    }

    @GetMapping
    public List<String> list(@RequestParam(value = "limit", defaultValue = "50") int limit) {
        requireAdmin();
        return agentAdminService.recentThoughts(Math.min(limit, 200));
    }

    @GetMapping("/stream")
    public SseEmitter stream() throws IOException {
        requireAdmin();
        SseEmitter emitter = new SseEmitter(30000L);
        List<String> thoughts = agentAdminService.recentThoughts(50);
        for (String thought : thoughts) {
            emitter.send(SseEmitter.event().name("thought").data(thought));
        }
        emitter.complete();
        return emitter;
    }

    private void requireAdmin() {
        var principal = com.taronote.common.security.SecurityUtils.requirePrincipal();
        if (!"ADMIN".equals(principal.type())) {
            throw new IllegalArgumentException("Admin access required");
        }
    }
}
