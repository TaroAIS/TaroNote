package com.taronote.content.web;

import com.taronote.content.domain.Comment;
import java.time.Instant;

// API 响应 DTO：隔离领域评论与外部契约。
public record CommentResponse(
        long id,
        String userId,
        long noteId,
        String content,
        Instant createdAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.id(),
                comment.userId(),
                comment.noteId(),
                comment.content(),
                comment.createdAt()
        );
    }
}
