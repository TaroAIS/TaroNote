package com.taronote.search.adapter;

import com.taronote.content.domain.Note;
import com.taronote.search.port.SearchPort;
import com.taronote.search.service.SearchMode;
import com.taronote.search.service.SearchService;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// 搜索适配器：把端口调用翻译到当前实现。
@Service
@ConditionalOnProperty(name = "taronote.adapter.mode", havingValue = "default", matchIfMissing = true)
public class SearchAdapter implements SearchPort {
    private final SearchService searchService;

    public SearchAdapter(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public List<Note> search(String query, SearchMode mode) {
        return searchService.search(query, mode);
    }
}
