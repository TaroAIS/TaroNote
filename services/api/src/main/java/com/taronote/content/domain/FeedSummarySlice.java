package com.taronote.content.domain;

import java.util.List;

// Feed 汇总分页：用于带互动统计的列表。
public record FeedSummarySlice(
        List<FeedItem> items,
        String nextCursor
) {
}
