package com.taronote.content.web;

import com.taronote.content.domain.Comment;
import java.time.Instant;

// API response DTO: decouples domain comment from external contract.
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
