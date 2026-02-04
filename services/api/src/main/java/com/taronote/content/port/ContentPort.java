package com.taronote.content.port;

import com.taronote.content.domain.FeedSlice;
import com.taronote.content.domain.Note;
import com.taronote.content.domain.NoteDetail;
import java.util.List;
import java.util.UUID;

// 内容能力端口：上游只依赖接口。
public interface ContentPort {
    Note create(UUID authorId, String title, String content, List<String> images);

    NoteDetail getDetail(long id);

    FeedSlice fetchFeed(String cursor, int limit);
}
