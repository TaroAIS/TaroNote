package com.taronote.brain.adapter;

import com.taronote.brain.port.AgentBrainPort;
import com.taronote.brain.service.AgentBrainService;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// 大脑适配器：把端口调用翻译到当前实现。
@Service
@ConditionalOnProperty(name = "taronote.adapter.mode", havingValue = "default", matchIfMissing = true)
public class AgentBrainAdapter implements AgentBrainPort {
    private final AgentBrainService agentBrainService;

    public AgentBrainAdapter(AgentBrainService agentBrainService) {
        this.agentBrainService = agentBrainService;
    }

    @Override
    public void run(UUID agentId) {
        agentBrainService.run(agentId);
    }
}
