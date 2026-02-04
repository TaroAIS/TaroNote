package com.taronote.content.web;

import com.taronote.common.security.SecurityUtils;
import com.taronote.content.port.ContentPort;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class NoteController {
    private final ContentPort contentPort;

    public NoteController(ContentPort contentPort) {
        this.contentPort = contentPort;
    }

    @PostMapping("/notes")
    public NoteResponse create(@Valid @RequestBody CreateNoteRequest request) {
        var userId = SecurityUtils.requireUserId();
        return NoteResponse.from(contentPort.create(userId, request.title(), request.content(), request.images(), request.tags()));
    }

    @GetMapping("/notes/{id}")
    public NoteDetailResponse getDetail(@PathVariable("id") long id) {
        // 映射领域详情为稳定响应 DTO。
        return NoteDetailResponse.from(contentPort.getDetail(id));
    }

    @GetMapping("/feed")
    public FeedResponse feed(@RequestParam(value = "cursor", required = false) String cursor,
                             @RequestParam(value = "limit", defaultValue = "20") int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 50);
        var slice = contentPort.fetchFeedSummary(cursor, safeLimit);
        List<NoteResponse> items = slice.items().stream()
                .map(item -> NoteResponse.from(item.note(), item.likeCount(), item.commentCount(), item.collectCount()))
                .toList();
        return new FeedResponse(items, slice.nextCursor());
    }
}
