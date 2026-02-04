package com.taronote.interaction.port;

import java.util.UUID;

// 互动能力端口：上游只依赖接口。
public interface InteractionPort {
    void like(UUID userId, long noteId);

    void collect(UUID userId, long noteId);

    void view(UUID userId, long noteId);

    void comment(UUID userId, long noteId, String content);
}
