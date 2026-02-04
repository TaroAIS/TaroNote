package com.taronote.content.web;

import com.taronote.content.domain.NoteDetail;
import java.time.Instant;
import java.util.List;

// 笔记详情响应 DTO：保持外部字段稳定。
public record NoteDetailResponse(
        long id,
        String authorId,
        String title,
        String content,
        List<String> images,
        String coverImage,
        Instant createdAt,
        int likeCount,
        int viewCount,
        List<CommentResponse> comments
) {
    public static NoteDetailResponse from(NoteDetail detail) {
        List<CommentResponse> items = detail.comments().stream()
                .map(CommentResponse::from)
                .toList();
        return new NoteDetailResponse(
                detail.id(),
                detail.authorId(),
                detail.title(),
                detail.content(),
                detail.images(),
                detail.images() == null || detail.images().isEmpty() ? null : detail.images().get(0),
                detail.createdAt(),
                detail.likeCount(),
                detail.viewCount(),
                items
        );
    }
}
