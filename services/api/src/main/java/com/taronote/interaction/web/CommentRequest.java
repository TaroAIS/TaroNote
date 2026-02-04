package com.taronote.interaction.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentRequest(
        @NotNull Long noteId,
        @NotBlank String content
) {
}
