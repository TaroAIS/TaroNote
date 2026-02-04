package com.taronote.content.adapter;

import com.taronote.content.domain.Note;
import com.taronote.content.domain.NoteDetail;
import com.taronote.content.port.ContentPort;
import com.taronote.content.service.NoteService;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// 内容适配器：把端口调用翻译到当前实现。
@Service
@ConditionalOnProperty(name = "taronote.adapter.mode", havingValue = "default", matchIfMissing = true)
public class ContentAdapter implements ContentPort {
    private final NoteService noteService;

    public ContentAdapter(NoteService noteService) {
        this.noteService = noteService;
    }

    @Override
    public Note create(UUID authorId, String title, String content, List<String> images) {
        return noteService.create(authorId, title, content, images);
    }

    @Override
    public NoteDetail getDetail(long id) {
        return noteService.getDetail(id);
    }

    @Override
    public List<Note> fetchFeed(Long cursor, int limit) {
        return noteService.fetchFeed(cursor, limit);
    }
}
