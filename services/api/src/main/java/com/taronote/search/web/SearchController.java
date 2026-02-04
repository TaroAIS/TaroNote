package com.taronote.search.web;

import com.taronote.content.web.NoteResponse;
import com.taronote.search.service.SearchMode;
import com.taronote.search.port.SearchPort;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final SearchPort searchPort;

    public SearchController(SearchPort searchPort) {
        this.searchPort = searchPort;
    }

    @GetMapping
    public SearchResponse search(@RequestParam("q") String query,
                                 @RequestParam(value = "mode", defaultValue = "HYBRID") SearchMode mode) {
        List<NoteResponse> items = searchPort.search(query, mode).stream()
                .map(NoteResponse::from)
                .toList();
        return new SearchResponse(items);
    }
}
