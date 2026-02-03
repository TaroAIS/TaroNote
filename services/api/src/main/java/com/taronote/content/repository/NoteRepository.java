package com.taronote.content.repository;

import com.taronote.common.util.JsonUtil;
import com.taronote.common.util.VectorUtil;
import com.taronote.content.domain.Comment;
import com.taronote.content.domain.Note;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class NoteRepository {
    private final JdbcTemplate jdbcTemplate;
    private final JsonUtil jsonUtil;

    public NoteRepository(JdbcTemplate jdbcTemplate, JsonUtil jsonUtil) {
        this.jdbcTemplate = jdbcTemplate;
        this.jsonUtil = jsonUtil;
    }

    public long create(UUID authorId, String title, String content, List<String> images, float[] embedding) {
        String imagesJson = jsonUtil.toJson(images);
        String vector = VectorUtil.toPgVector(embedding);
        return jdbcTemplate.queryForObject(
                "INSERT INTO notes (author_id, title, content, images, embedding) "
                        + "VALUES (?, ?, ?, ?::jsonb, ?::vector) RETURNING id",
                Long.class,
                authorId, title, content, imagesJson, vector
        );
    }

    public Optional<Note> findById(long id) {
        List<Note> rows = jdbcTemplate.query("SELECT * FROM notes WHERE id = ?", noteRowMapper(), id);
        return rows.stream().findFirst();
    }

    public List<Note> fetchFeed(Long cursor, int limit) {
        if (cursor == null) {
            return jdbcTemplate.query(
                    "SELECT * FROM notes ORDER BY created_at DESC, id DESC LIMIT ?",
                    noteRowMapper(),
                    limit
            );
        }
        return jdbcTemplate.query(
                "SELECT * FROM notes WHERE id < ? ORDER BY created_at DESC, id DESC LIMIT ?",
                noteRowMapper(),
                cursor, limit
        );
    }

    public List<Comment> fetchComments(long noteId) {
        return jdbcTemplate.query(
                "SELECT id, user_id, note_id, content, created_at FROM interactions "
                        + "WHERE note_id = ? AND action_type = 'COMMENT' ORDER BY created_at ASC",
                (rs, rowNum) -> new Comment(
                        rs.getLong("id"),
                        rs.getString("user_id"),
                        rs.getLong("note_id"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at").toInstant()
                ),
                noteId
        );
    }

    public int countByAction(long noteId, String actionType) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM interactions WHERE note_id = ? AND action_type = ?",
                Integer.class,
                noteId, actionType
        );
        return count == null ? 0 : count;
    }

    private RowMapper<Note> noteRowMapper() {
        return (rs, rowNum) -> mapNote(rs);
    }

    private Note mapNote(ResultSet rs) throws SQLException {
        String imagesJson = rs.getString("images");
        List<String> images = jsonUtil.readStringList(imagesJson);
        return new Note(
                rs.getLong("id"),
                UUID.fromString(rs.getString("author_id")),
                rs.getString("title"),
                rs.getString("content"),
                images,
                rs.getTimestamp("created_at").toInstant()
        );
    }
}
