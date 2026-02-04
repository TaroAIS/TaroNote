package com.taronote.content.service;

import com.taronote.common.ai.EmbeddingService;
import com.taronote.common.moderation.ModerationService;
import com.taronote.content.domain.Comment;
import com.taronote.content.domain.FeedCursor;
import com.taronote.content.domain.FeedItem;
import com.taronote.content.domain.FeedSlice;
import com.taronote.content.domain.FeedSummarySlice;
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

    public Note create(UUID authorId, String title, String content, List<String> images, List<String> tags) {
        moderationService.assertSafe(title);
        moderationService.assertSafe(content);
        float[] embedding = embeddingService.embed(title + " " + (content == null ? "" : content));
        List<String> safeImages = images == null ? List.of() : images;
        List<String> safeTags = tags == null ? List.of() : tags;
        String coverImage = safeImages.isEmpty() ? null : safeImages.get(0);
        long id = noteRepository.create(authorId, title, content, safeImages, coverImage, safeTags, embedding);
        return noteRepository.findById(id).orElseThrow();
    }

    public NoteDetail getDetail(long id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Note not found"));
        int likeCount = noteRepository.countByAction(id, "LIKE");
        int viewCount = noteRepository.countByAction(id, "VIEW");
        int commentCount = noteRepository.countByAction(id, "COMMENT");
        int collectCount = noteRepository.countByAction(id, "COLLECT");
        List<Comment> comments = noteRepository.fetchComments(id);
        return new NoteDetail(
                note.id(),
                note.authorId().toString(),
                note.title(),
                note.content(),
                note.images(),
                note.coverImage(),
                note.tags(),
                note.createdAt(),
                likeCount,
                commentCount,
                collectCount,
                viewCount,
                comments
        );
    }

    public FeedSlice fetchFeed(String cursor, int limit) {
        FeedCursor feedCursor = parseCursor(cursor);
        List<Note> items = noteRepository.fetchFeed(feedCursor, limit);
        String nextCursor = items.isEmpty() ? null : formatCursor(items.get(items.size() - 1));
        return new FeedSlice(items, nextCursor);
    }

    public FeedSummarySlice fetchFeedSummary(String cursor, int limit) {
        FeedCursor feedCursor = parseCursor(cursor);
        List<NoteRepository.FeedItemRow> rows = noteRepository.fetchFeedSummary(feedCursor, limit);
        List<FeedItem> items = rows.stream()
                .map(row -> new FeedItem(row.note(), row.likeCount(), row.commentCount(), row.collectCount()))
                .toList();
        String nextCursor = rows.isEmpty() ? null : formatCursor(rows.get(rows.size() - 1).note());
        return new FeedSummarySlice(items, nextCursor);
    }

    private FeedCursor parseCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        if (cursor.contains("|")) {
            String[] parts = cursor.split("\\|", 2);
            if (parts.length == 2) {
                try {
                    long createdAtMillis = Long.parseLong(parts[0]);
                    long id = Long.parseLong(parts[1]);
                    return new FeedCursor(java.time.Instant.ofEpochMilli(createdAtMillis), id);
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
            return null;
        }
        try {
            long id = Long.parseLong(cursor);
            return noteRepository.findFeedCursorById(id).orElse(null);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private String formatCursor(Note note) {
        long createdAtMillis = note.createdAt().toEpochMilli();
        return createdAtMillis + "|" + note.id();
    }
}
