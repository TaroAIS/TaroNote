package com.taronote.admin.port;

import com.taronote.admin.web.AgentStatus;
import java.util.List;

// 管理能力端口：对外提供监控与干预操作。
public interface AdminPort {
    List<AgentStatus> listAgents();

    void freezeAgent(String agentId);

    void unfreezeAgent(String agentId);

    boolean isFrozen(String agentId);

    void injectDirective(String agentId, String directive);

    void appendThought(String agentId, String thought);

    List<String> recentThoughts(int limit);
}
