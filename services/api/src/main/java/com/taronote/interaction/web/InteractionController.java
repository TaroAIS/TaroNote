package com.taronote.interaction.web;

import com.taronote.common.security.SecurityUtils;
import com.taronote.interaction.service.InteractionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interactions")
public class InteractionController {
    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping("/like")
    public void like(@Valid @RequestBody NoteActionRequest request) {
        interactionService.like(SecurityUtils.requireUserId(), request.noteId());
    }

    @PostMapping("/collect")
    public void collect(@Valid @RequestBody NoteActionRequest request) {
        interactionService.collect(SecurityUtils.requireUserId(), request.noteId());
    }

    @PostMapping("/comment")
    public void comment(@Valid @RequestBody CommentRequest request) {
        interactionService.comment(SecurityUtils.requireUserId(), request.noteId(), request.content());
    }
}
