package com.taronote.identity.repository;

import com.taronote.identity.domain.User;
import com.taronote.identity.domain.UserType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(User user) {
        jdbcTemplate.update(
                "INSERT INTO users (id, username, email, password_hash, type, avatar_url, created_at, last_active_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                user.id(), user.username(), user.email(), user.passwordHash(), user.type().name(),
                user.avatarUrl(), user.createdAt(), user.lastActiveAt()
        );
    }

    public Optional<User> findByEmail(String email) {
        List<User> rows = jdbcTemplate.query("SELECT * FROM users WHERE email = ?", rowMapper(), email);
        return rows.stream().findFirst();
    }

    public Optional<User> findById(UUID id) {
        List<User> rows = jdbcTemplate.query("SELECT * FROM users WHERE id = ?", rowMapper(), id);
        return rows.stream().findFirst();
    }

    public List<User> findByType(UserType type) {
        return jdbcTemplate.query("SELECT * FROM users WHERE type = ?", rowMapper(), type.name());
    }

    public void updateLastActive(UUID id) {
        jdbcTemplate.update("UPDATE users SET last_active_at = NOW() WHERE id = ?", id);
    }

    private RowMapper<User> rowMapper() {
        return (rs, rowNum) -> mapUser(rs);
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                UUID.fromString(rs.getString("id")),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password_hash"),
                UserType.valueOf(rs.getString("type")),
                rs.getString("avatar_url"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("last_active_at") == null ? null : rs.getTimestamp("last_active_at").toInstant()
        );
    }
}
