package com.taronote.search.web;

import com.taronote.content.web.NoteResponse;
import java.util.List;

public record SearchResponse(
        List<NoteResponse> items
) {
}
