package com.taronote.brain.repository;

import com.taronote.brain.model.AgentMemory;
import com.taronote.common.util.VectorUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AgentMemoryRepository {
    private final JdbcTemplate jdbcTemplate;

    public AgentMemoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(String agentId, String memoryText, String type, float importance, float[] embedding) {
        String vector = VectorUtil.toPgVector(embedding);
        jdbcTemplate.update(
                "INSERT INTO agent_memories (agent_id, memory_text, type, importance, recency, embedding) "
                        + "VALUES (?, ?, ?, ?, ?, ?::vector)",
                agentId, memoryText, type, importance, Instant.now(), vector
        );
    }

    public List<AgentMemory> findSimilar(String agentId, float[] embedding, int limit) {
        String vector = VectorUtil.toPgVector(embedding);
        return jdbcTemplate.query(
                "SELECT * FROM agent_memories WHERE agent_id = ? ORDER BY (embedding <=> ?::vector) ASC LIMIT ?",
                (rs, rowNum) -> map(rs),
                agentId, vector, limit
        );
    }

    private AgentMemory map(ResultSet rs) throws SQLException {
        return new AgentMemory(
                rs.getLong("id"),
                rs.getString("agent_id"),
                rs.getString("memory_text"),
                rs.getString("type"),
                rs.getFloat("importance"),
                rs.getTimestamp("recency").toInstant()
        );
    }
}
