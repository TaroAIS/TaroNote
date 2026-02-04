package com.taronote.content.domain;

import java.time.Instant;
import java.util.List;

public record NoteDetail(
        long id,
        String authorId,
        String title,
        String content,
        List<String> images,
        String coverImage,
        List<String> tags,
        Instant createdAt,
        int likeCount,
        int viewCount,
        List<Comment> comments
) {
}
