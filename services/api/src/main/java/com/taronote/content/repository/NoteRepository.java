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

    public long create(UUID authorId, String title, String content, List<String> images, String coverImage, List<String> tags, float[] embedding) {
        String imagesJson = jsonUtil.toJson(images);
        String tagsJson = jsonUtil.toJson(tags);
        String vector = VectorUtil.toPgVector(embedding);
        return jdbcTemplate.queryForObject(
                "INSERT INTO notes (author_id, title, content, images, cover_image, tags, embedding) "
                        + "VALUES (?, ?, ?, ?::jsonb, ?, ?::jsonb, ?::vector) RETURNING id",
                Long.class,
                authorId, title, content, imagesJson, coverImage, tagsJson, vector
        );
    }

    public Optional<Note> findById(long id) {
        List<Note> rows = jdbcTemplate.query("SELECT * FROM notes WHERE id = ?", noteRowMapper(), id);
        return rows.stream().findFirst();
    }

    public List<Note> fetchFeed(FeedCursor cursor, int limit) {
        if (cursor == null) {
            return jdbcTemplate.query(
                    "SELECT * FROM notes ORDER BY created_at DESC, id DESC LIMIT ?",
                    noteRowMapper(),
                    limit
            );
        }
        return jdbcTemplate.query(
                "SELECT * FROM notes "
                        + "WHERE (created_at, id) < (?::timestamptz, ?::bigint) "
                        + "ORDER BY created_at DESC, id DESC LIMIT ?",
                noteRowMapper(),
                Timestamp.from(cursor.createdAt()), cursor.id(), limit
        );
    }

    public List<FeedItemRow> fetchFeedSummary(FeedCursor cursor, int limit) {
        if (cursor == null) {
            return jdbcTemplate.query(
                    "SELECT n.*, "
                            + "COALESCE(s.like_count, 0) AS like_count, "
                            + "COALESCE(s.comment_count, 0) AS comment_count, "
                            + "COALESCE(s.collect_count, 0) AS collect_count "
                            + "FROM notes n "
                            + "LEFT JOIN ("
                            + "  SELECT note_id, "
                            + "  SUM(CASE WHEN action_type = 'LIKE' THEN 1 ELSE 0 END) AS like_count, "
                            + "  SUM(CASE WHEN action_type = 'COMMENT' THEN 1 ELSE 0 END) AS comment_count, "
                            + "  SUM(CASE WHEN action_type = 'COLLECT' THEN 1 ELSE 0 END) AS collect_count "
                            + "  FROM interactions GROUP BY note_id"
                            + ") s ON s.note_id = n.id "
                            + "ORDER BY n.created_at DESC, n.id DESC LIMIT ?",
                    feedItemRowMapper(),
                    limit
            );
        }
        return jdbcTemplate.query(
                "SELECT n.*, "
                        + "COALESCE(s.like_count, 0) AS like_count, "
                        + "COALESCE(s.comment_count, 0) AS comment_count, "
                        + "COALESCE(s.collect_count, 0) AS collect_count "
                        + "FROM notes n "
                        + "LEFT JOIN ("
                        + "  SELECT note_id, "
                        + "  SUM(CASE WHEN action_type = 'LIKE' THEN 1 ELSE 0 END) AS like_count, "
                        + "  SUM(CASE WHEN action_type = 'COMMENT' THEN 1 ELSE 0 END) AS comment_count, "
                        + "  SUM(CASE WHEN action_type = 'COLLECT' THEN 1 ELSE 0 END) AS collect_count "
                        + "  FROM interactions GROUP BY note_id"
                        + ") s ON s.note_id = n.id "
                        + "WHERE (n.created_at, n.id) < (?::timestamptz, ?::bigint) "
                        + "ORDER BY n.created_at DESC, n.id DESC LIMIT ?",
                feedItemRowMapper(),
                Timestamp.from(cursor.createdAt()), cursor.id(), limit
        );
    }

    public Optional<FeedCursor> findFeedCursorById(long id) {
        List<FeedCursor> rows = jdbcTemplate.query(
                "SELECT created_at, id FROM notes WHERE id = ?",
                (rs, rowNum) -> new FeedCursor(
                        rs.getTimestamp("created_at").toInstant(),
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

    private RowMapper<FeedItemRow> feedItemRowMapper() {
        return (rs, rowNum) -> new FeedItemRow(
                mapNote(rs),
                rs.getInt("like_count"),
                rs.getInt("comment_count"),
                rs.getInt("collect_count")
        );
    }


    private Note mapNote(ResultSet rs) throws SQLException {
        String imagesJson = rs.getString("images");
        List<String> images = jsonUtil.readStringList(imagesJson);
        String tagsJson = rs.getString("tags");
        List<String> tags = jsonUtil.readStringList(tagsJson);
        return new Note(
                rs.getLong("id"),
                UUID.fromString(rs.getString("author_id")),
                rs.getString("title"),
                rs.getString("content"),
                images,
                rs.getString("cover_image"),
                tags,
                rs.getTimestamp("created_at").toInstant()
        );
    }

    public record FeedItemRow(Note note, int likeCount, int commentCount, int collectCount) {
    }

}
