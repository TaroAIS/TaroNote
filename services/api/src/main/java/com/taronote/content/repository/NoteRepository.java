package com.taronote.content.repository;

import com.taronote.common.util.JsonUtil;
import com.taronote.common.util.VectorUtil;
import com.taronote.content.domain.Comment;
import com.taronote.content.domain.FeedCursor;
import com.taronote.content.domain.Note;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

    public List<FeedRow> fetchFeed(FeedCursor cursor, int limit) {
        if (cursor == null) {
            return jdbcTemplate.query(
                    "SELECT n.*, COALESCE(s.score, 0) AS score FROM notes n "
                            + "LEFT JOIN ("
                            + "  SELECT note_id, "
                            + "  SUM(CASE action_type "
                            + "    WHEN 'LIKE' THEN 3 "
                            + "    WHEN 'COMMENT' THEN 2 "
                            + "    WHEN 'COLLECT' THEN 4 "
                            + "    WHEN 'VIEW' THEN 1 "
                            + "    ELSE 0 END)::bigint AS score "
                            + "  FROM interactions GROUP BY note_id"
                            + ") s ON s.note_id = n.id "
                            + "ORDER BY n.created_at DESC, COALESCE(s.score, 0) DESC, n.id DESC LIMIT ?",
                    feedRowMapper(),
                    limit
            );
        }
        return jdbcTemplate.query(
                "SELECT n.*, COALESCE(s.score, 0) AS score FROM notes n "
                        + "LEFT JOIN ("
                        + "  SELECT note_id, "
                        + "  SUM(CASE action_type "
                        + "    WHEN 'LIKE' THEN 3 "
                        + "    WHEN 'COMMENT' THEN 2 "
                        + "    WHEN 'COLLECT' THEN 4 "
                        + "    WHEN 'VIEW' THEN 1 "
                        + "    ELSE 0 END)::bigint AS score "
                        + "  FROM interactions GROUP BY note_id"
                        + ") s ON s.note_id = n.id "
                        + "WHERE (n.created_at, COALESCE(s.score, 0), n.id) < (?::timestamptz, ?::bigint, ?::bigint) "
                        + "ORDER BY n.created_at DESC, COALESCE(s.score, 0) DESC, n.id DESC LIMIT ?",
                feedRowMapper(),
                Timestamp.from(cursor.createdAt()), cursor.score(), cursor.id(), limit
        );
    }

    public Optional<FeedCursor> findFeedCursorById(long id) {
        List<FeedCursor> rows = jdbcTemplate.query(
                "SELECT n.created_at, COALESCE(s.score, 0) AS score, n.id FROM notes n "
                        + "LEFT JOIN ("
                        + "  SELECT note_id, "
                        + "  SUM(CASE action_type "
                        + "    WHEN 'LIKE' THEN 3 "
                        + "    WHEN 'COMMENT' THEN 2 "
                        + "    WHEN 'COLLECT' THEN 4 "
                        + "    WHEN 'VIEW' THEN 1 "
                        + "    ELSE 0 END)::bigint AS score "
                        + "  FROM interactions GROUP BY note_id"
                        + ") s ON s.note_id = n.id "
                        + "WHERE n.id = ?",
                (rs, rowNum) -> new FeedCursor(
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getLong("score"),
                        rs.getLong("id")
                ),
                id
        );
        return rows.stream().findFirst();
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

    private RowMapper<FeedRow> feedRowMapper() {
        return (rs, rowNum) -> new FeedRow(mapNote(rs), rs.getLong("score"));
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

    public record FeedRow(Note note, long score) {
    }
}
