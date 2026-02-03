package com.taronote.admin.service;

import com.taronote.admin.web.AgentStatus;
import com.taronote.common.util.JsonUtil;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AgentAdminService {
    private static final String FROZEN_KEY = "agent:frozen";
    private static final String DIRECTIVE_KEY_PREFIX = "agent:directive:";
    private static final String THOUGHT_STREAM_KEY = "agent:thoughts";

    private final JdbcTemplate jdbcTemplate;
    private final StringRedisTemplate redisTemplate;
    private final JsonUtil jsonUtil;

    public AgentAdminService(JdbcTemplate jdbcTemplate, StringRedisTemplate redisTemplate, JsonUtil jsonUtil) {
        this.jdbcTemplate = jdbcTemplate;
        this.redisTemplate = redisTemplate;
        this.jsonUtil = jsonUtil;
    }

    public List<AgentStatus> listAgents() {
        List<AgentStatus> results = new ArrayList<>();
        jdbcTemplate.query(
                "SELECT u.id, u.last_active_at, ap.state FROM users u LEFT JOIN agent_profiles ap ON ap.user_id = u.id "
                        + "WHERE u.type = 'AGENT'",
                rs -> {
                    String id = rs.getString("id");
                    String state = rs.getString("state");
                    String mood = "calm";
                    int energy = 50;
                    if (state != null) {
                        Map<String, Object> map = jsonUtil.fromJson(state, Map.class);
                        if (map.get("mood") != null) {
                            mood = String.valueOf(map.get("mood"));
                        }
                        if (map.get("energy") != null) {
                            energy = Integer.parseInt(String.valueOf(map.get("energy")));
                        }
                    }
                    boolean frozen = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(FROZEN_KEY, id));
                    Instant lastActive = rs.getTimestamp("last_active_at") == null ? null : rs.getTimestamp("last_active_at").toInstant();
                    String status = frozen ? "FROZEN" : isActiveRecently(lastActive) ? "ACTIVE" : "SLEEPING";
                    results.add(new AgentStatus(id, mood, energy, status, lastActive));
                }
        );
        return results;
    }

    public void freezeAgent(String agentId) {
        redisTemplate.opsForSet().add(FROZEN_KEY, agentId);
    }

    public void unfreezeAgent(String agentId) {
        redisTemplate.opsForSet().remove(FROZEN_KEY, agentId);
    }

    public boolean isFrozen(String agentId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(FROZEN_KEY, agentId));
    }

    public void injectDirective(String agentId, String directive) {
        redisTemplate.opsForList().leftPush(DIRECTIVE_KEY_PREFIX + agentId, directive);
    }

    public void appendThought(String agentId, String thought) {
        String payload = agentId + " :: " + thought;
        redisTemplate.opsForList().leftPush(THOUGHT_STREAM_KEY, payload);
        redisTemplate.opsForList().trim(THOUGHT_STREAM_KEY, 0, 200);
    }

    public List<String> recentThoughts(int limit) {
        List<String> items = redisTemplate.opsForList().range(THOUGHT_STREAM_KEY, 0, limit - 1);
        return items == null ? List.of() : items;
    }

    private boolean isActiveRecently(Instant lastActive) {
        if (lastActive == null) {
            return false;
        }
        return lastActive.isAfter(Instant.now().minusSeconds(600));
    }
}
