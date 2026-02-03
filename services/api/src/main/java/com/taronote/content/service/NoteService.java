package com.taronote.content.service;

import com.taronote.common.ai.EmbeddingService;
import com.taronote.common.moderation.ModerationService;
import com.taronote.content.domain.Comment;
import com.taronote.content.domain.Note;
import com.taronote.content.domain.NoteDetail;
import com.taronote.content.repository.NoteRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final EmbeddingService embeddingService;
    private final ModerationService moderationService;

    public NoteService(NoteRepository noteRepository, EmbeddingService embeddingService, ModerationService moderationService) {
        this.noteRepository = noteRepository;
        this.embeddingService = embeddingService;
        this.moderationService = moderationService;
    }

    public Note create(UUID authorId, String title, String content, List<String> images) {
        moderationService.assertSafe(title);
        moderationService.assertSafe(content);
        float[] embedding = embeddingService.embed(title + " " + (content == null ? "" : content));
        List<String> safeImages = images == null ? List.of() : images;
        long id = noteRepository.create(authorId, title, content, safeImages, embedding);
        return noteRepository.findById(id).orElseThrow();
    }

    public NoteDetail getDetail(long id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Note not found"));
        int likeCount = noteRepository.countByAction(id, "LIKE");
        int viewCount = noteRepository.countByAction(id, "VIEW");
        List<Comment> comments = noteRepository.fetchComments(id);
        return new NoteDetail(
                note.id(),
                note.authorId().toString(),
                note.title(),
                note.content(),
                note.images(),
                note.createdAt(),
                likeCount,
                viewCount,
                comments
        );
    }

    public List<Note> fetchFeed(Long cursor, int limit) {
        return noteRepository.fetchFeed(cursor, limit);
    }
}
