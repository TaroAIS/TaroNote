package com.taronote.search.adapter;

import com.taronote.content.domain.Note;
import com.taronote.common.port.LoggingPort;
import com.taronote.search.port.SearchPort;
import com.taronote.search.service.SearchMode;
import com.taronote.search.service.SearchService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// 搜索适配器：把端口调用翻译到当前实现。
@Service
@ConditionalOnProperty(name = "taronote.adapter.mode", havingValue = "default", matchIfMissing = true)
public class SearchAdapter implements SearchPort {
    private final SearchService searchService;
    private final LoggingPort loggingPort;

    public SearchAdapter(SearchService searchService, LoggingPort loggingPort) {
        this.searchService = searchService;
        this.loggingPort = loggingPort;
    }

    @Override
    public List<Note> search(String query, SearchMode mode) {
        List<Note> results = searchService.search(query, mode);
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("mode", mode == null ? "UNKNOWN" : mode.name());
        fields.put("queryLength", query == null ? 0 : query.length());
        fields.put("resultCount", results.size());
        loggingPort.info("search.query", fields);
        return results;
    }
}
