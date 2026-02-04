package com.taronote.search.port;

import com.taronote.content.domain.Note;
import com.taronote.search.service.SearchMode;
import java.util.List;

// 搜索能力端口：屏蔽底层检索引擎细节。
public interface SearchPort {
    List<Note> search(String query, SearchMode mode);
}
