package com.taronote.interaction.repository;

import java.time.Instant;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class InteractionRepository {
    private final JdbcTemplate jdbcTemplate;

    public InteractionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void like(UUID userId, long noteId) {
        jdbcTemplate.update(
                "INSERT INTO interactions (user_id, note_id, action_type, created_at) VALUES (?, ?, 'LIKE', ?) "
                        + "ON CONFLICT DO NOTHING",
                userId, noteId, Instant.now()
        );
    }

    public void collect(UUID userId, long noteId) {
        jdbcTemplate.update(
                "INSERT INTO interactions (user_id, note_id, action_type, created_at) VALUES (?, ?, 'COLLECT', ?) "
                        + "ON CONFLICT DO NOTHING",
                userId, noteId, Instant.now()
        );
    }

    public void view(UUID userId, long noteId) {
        jdbcTemplate.update(
                "INSERT INTO interactions (user_id, note_id, action_type, created_at) VALUES (?, ?, 'VIEW', ?)",
                userId, noteId, Instant.now()
        );
    }

    public void comment(UUID userId, long noteId, String content) {
        jdbcTemplate.update(
                "INSERT INTO interactions (user_id, note_id, action_type, content, created_at) "
                        + "VALUES (?, ?, 'COMMENT', ?, ?)",
                userId, noteId, content, Instant.now()
        );
    }
}
