package com.taronote.content.adapter;

import com.taronote.content.domain.FeedSlice;
import com.taronote.content.domain.Note;
import com.taronote.content.domain.NoteDetail;
import com.taronote.content.port.ContentPort;
import com.taronote.content.service.NoteService;
import com.taronote.common.port.LoggingPort;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// 内容适配器：把端口调用翻译到当前实现。
@Service
@ConditionalOnProperty(name = "taronote.adapter.mode", havingValue = "default", matchIfMissing = true)
public class ContentAdapter implements ContentPort {
    private final NoteService noteService;
    private final LoggingPort loggingPort;

    public ContentAdapter(NoteService noteService, LoggingPort loggingPort) {
        this.noteService = noteService;
        this.loggingPort = loggingPort;
    }

    @Override
    public Note create(UUID authorId, String title, String content, List<String> images) {
        Note note = noteService.create(authorId, title, content, images);
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("authorId", authorId);
        fields.put("noteId", note.id());
        fields.put("titleLength", title == null ? 0 : title.length());
        fields.put("hasImages", images != null && !images.isEmpty());
        loggingPort.info("content.create", fields);
        return note;
    }

    @Override
    public NoteDetail getDetail(long id) {
        NoteDetail detail = noteService.getDetail(id);
        loggingPort.info("content.detail", Map.of("noteId", id));
        return detail;
    }

    @Override
    public FeedSlice fetchFeed(String cursor, int limit) {
        FeedSlice slice = noteService.fetchFeed(cursor, limit);
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("cursor", cursor);
        fields.put("limit", limit);
        fields.put("resultCount", slice.items().size());
        loggingPort.info("content.feed", fields);
        return slice;
    }
}
