package com.taronote.search.service;

import com.taronote.common.ai.EmbeddingService;
import com.taronote.common.util.JsonUtil;
import com.taronote.common.util.VectorUtil;
import com.taronote.content.domain.Note;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingService embeddingService;
    private final JsonUtil jsonUtil;

    public SearchService(JdbcTemplate jdbcTemplate, EmbeddingService embeddingService, JsonUtil jsonUtil) {
        this.jdbcTemplate = jdbcTemplate;
        this.embeddingService = embeddingService;
        this.jsonUtil = jsonUtil;
    }

    public List<Note> search(String query, SearchMode mode) {
        if (mode == SearchMode.KEYWORD) {
            return keywordSearch(query);
        }
        if (mode == SearchMode.SEMANTIC) {
            return semanticSearch(query);
        }
        return hybridSearch(query);
    }

    private List<Note> keywordSearch(String query) {
        String normalized = query.toLowerCase(Locale.ROOT);
        return jdbcTemplate.query(
                "SELECT n.* FROM notes n, plainto_tsquery('simple', ?) q "
                        + "WHERE n.search_vector @@ q "
                        + "ORDER BY ts_rank_cd(n.search_vector, q) DESC, n.created_at DESC LIMIT 50",
                noteRowMapper(),
                normalized
        );
    }

    private List<Note> semanticSearch(String query) {
        float[] embedding = embeddingService.embed(query);
        String vector = VectorUtil.toPgVector(embedding);
        return jdbcTemplate.query(
                "SELECT * FROM notes WHERE embedding IS NOT NULL ORDER BY (embedding <=> ?::vector) ASC LIMIT 50",
                noteRowMapper(),
                vector
        );
    }

    private List<Note> hybridSearch(String query) {
        String normalized = query.toLowerCase(Locale.ROOT);
        float[] embedding = embeddingService.embed(query);
        String vector = VectorUtil.toPgVector(embedding);
        return jdbcTemplate.query(
                "SELECT n.* FROM notes n, plainto_tsquery('simple', ?) q "
                        + "WHERE (n.search_vector @@ q) OR n.embedding IS NOT NULL "
                        + "ORDER BY (CASE WHEN n.search_vector @@ q THEN 0 ELSE 1 END), "
                        + "ts_rank_cd(n.search_vector, q) DESC, "
                        + "COALESCE((n.embedding <=> ?::vector), 1e9) ASC, "
                        + "n.created_at DESC LIMIT 50",
                noteRowMapper(),
                normalized, vector
        );
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
