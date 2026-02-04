package com.taronote.content.web;

import com.taronote.content.domain.Note;
import java.time.Instant;
import java.util.List;

public record NoteResponse(
        long id,
        String authorId,
        String title,
        String content,
        List<String> images,
        String coverImage,
        List<String> tags,
        int likeCount,
        int commentCount,
        int collectCount,
        Instant createdAt
) {
    public static NoteResponse from(Note note) {
        return new NoteResponse(
                note.id(),
                note.authorId().toString(),
                note.title(),
                note.content(),
                note.images(),
                note.coverImage(),
                note.tags(),
                0,
                0,
                0,
                note.createdAt()
        );
    }

    public static NoteResponse from(Note note, int likeCount, int commentCount, int collectCount) {
        return new NoteResponse(
                note.id(),
                note.authorId().toString(),
                note.title(),
                note.content(),
                note.images(),
                note.coverImage(),
                note.tags(),
                likeCount,
                commentCount,
                collectCount,
                note.createdAt()
        );
    }
}
