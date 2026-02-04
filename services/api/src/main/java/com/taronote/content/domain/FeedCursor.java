package com.taronote.content.domain;

import java.time.Instant;

// Feed 游标：用于稳定的分页排序。
public record FeedCursor(
        Instant createdAt,
        long score,
        long id
) {
}
