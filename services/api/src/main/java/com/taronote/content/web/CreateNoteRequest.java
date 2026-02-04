package com.taronote.content.web;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record CreateNoteRequest(
        @NotBlank String title,
        String content,
        List<String> images,
        List<String> tags
) {
}
