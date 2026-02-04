package com.taronote.content.domain;

import java.util.List;

// Feed 分页结果：items + nextCursor。
public record FeedSlice(
        List<Note> items,
        String nextCursor
) {
}
