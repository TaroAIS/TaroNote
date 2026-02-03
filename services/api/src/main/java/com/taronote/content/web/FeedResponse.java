package com.taronote.content.web;

import java.util.List;

public record FeedResponse(
        List<NoteResponse> items,
        String nextCursor
) {
}
