package com.taronote.interaction.web;

import com.taronote.common.security.SecurityUtils;
import com.taronote.interaction.port.InteractionPort;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interactions")
public class InteractionController {
    private final InteractionPort interactionPort;

    public InteractionController(InteractionPort interactionPort) {
        this.interactionPort = interactionPort;
    }

    @PostMapping("/like")
    public void like(@Valid @RequestBody NoteActionRequest request) {
        interactionPort.like(SecurityUtils.requireUserId(), request.noteId());
    }

    @PostMapping("/collect")
    public void collect(@Valid @RequestBody NoteActionRequest request) {
        interactionPort.collect(SecurityUtils.requireUserId(), request.noteId());
    }

    @PostMapping("/comment")
    public void comment(@Valid @RequestBody CommentRequest request) {
        interactionPort.comment(SecurityUtils.requireUserId(), request.noteId(), request.content());
    }
}
