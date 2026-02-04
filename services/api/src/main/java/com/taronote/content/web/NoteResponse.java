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
                note.createdAt()
        );
    }
}
