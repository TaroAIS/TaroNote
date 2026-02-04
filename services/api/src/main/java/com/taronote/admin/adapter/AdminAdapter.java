package com.taronote.admin.adapter;

import com.taronote.admin.port.AdminPort;
import com.taronote.admin.service.AgentAdminService;
import com.taronote.admin.web.AgentStatus;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// 管理适配器：把端口调用翻译到当前实现。
@Service
@ConditionalOnProperty(name = "taronote.adapter.mode", havingValue = "default", matchIfMissing = true)
public class AdminAdapter implements AdminPort {
    private final AgentAdminService agentAdminService;

    public AdminAdapter(AgentAdminService agentAdminService) {
        this.agentAdminService = agentAdminService;
    }

    @Override
    public List<AgentStatus> listAgents() {
        return agentAdminService.listAgents();
    }

    @Override
    public void freezeAgent(String agentId) {
        agentAdminService.freezeAgent(agentId);
    }

    @Override
    public void unfreezeAgent(String agentId) {
        agentAdminService.unfreezeAgent(agentId);
    }

    @Override
    public boolean isFrozen(String agentId) {
        return agentAdminService.isFrozen(agentId);
    }

    @Override
    public void injectDirective(String agentId, String directive) {
        agentAdminService.injectDirective(agentId, directive);
    }

    @Override
    public void appendThought(String agentId, String thought) {
        agentAdminService.appendThought(agentId, thought);
    }

    @Override
    public List<String> recentThoughts(int limit) {
        return agentAdminService.recentThoughts(limit);
    }
}
