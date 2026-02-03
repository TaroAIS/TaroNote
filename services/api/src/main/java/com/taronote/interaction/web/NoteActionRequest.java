package com.taronote.interaction.web;

import jakarta.validation.constraints.NotNull;

public record NoteActionRequest(
        @NotNull Long noteId
) {
}
