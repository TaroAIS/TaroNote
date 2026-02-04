package com.taronote.brain.port;

import java.util.UUID;

// 大脑能力端口：对外暴露运行入口。
public interface AgentBrainPort {
    void run(UUID agentId);
}
