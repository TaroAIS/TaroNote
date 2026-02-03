package com.taronote.admin.web;

import com.taronote.admin.service.AgentAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/agents")
public class AdminAgentController {
    private final AgentAdminService agentAdminService;

    public AdminAgentController(AgentAdminService agentAdminService) {
        this.agentAdminService = agentAdminService;
    }

    @GetMapping("/active")
    public ActiveAgentsResponse listActive() {
        requireAdmin();
        return new ActiveAgentsResponse(agentAdminService.listAgents());
    }

    @PostMapping("/{id}/freeze")
    public void freeze(@PathVariable("id") String id) {
        requireAdmin();
        agentAdminService.freezeAgent(id);
    }

    @PostMapping("/{id}/inject")
    public void inject(@PathVariable("id") String id, @Valid @RequestBody InjectRequest request) {
        requireAdmin();
        agentAdminService.injectDirective(id, request.directive());
    }

    private void requireAdmin() {
        var principal = com.taronote.common.security.SecurityUtils.requirePrincipal();
        if (!"ADMIN".equals(principal.type())) {
            throw new IllegalArgumentException("Admin access required");
        }
    }
}
