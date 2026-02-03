package com.taronote.admin.web;

import java.util.List;

public record ActiveAgentsResponse(
        List<AgentStatus> agents
) {
}
