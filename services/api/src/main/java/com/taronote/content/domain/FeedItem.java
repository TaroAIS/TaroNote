package com.taronote.content.domain;

// Feed 列表项：包含互动统计。
public record FeedItem(
        Note note,
        int likeCount,
        int commentCount,
        int collectCount,
        String authorName,
        String authorAvatar
) {
}
