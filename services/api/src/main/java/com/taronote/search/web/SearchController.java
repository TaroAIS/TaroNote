package com.taronote.search.web;

import com.taronote.content.web.NoteResponse;
import com.taronote.search.service.SearchMode;
import com.taronote.search.service.SearchService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public SearchResponse search(@RequestParam("q") String query,
                                 @RequestParam(value = "mode", defaultValue = "HYBRID") SearchMode mode) {
        List<NoteResponse> items = searchService.search(query, mode).stream()
                .map(NoteResponse::from)
                .toList();
        return new SearchResponse(items);
    }
}
