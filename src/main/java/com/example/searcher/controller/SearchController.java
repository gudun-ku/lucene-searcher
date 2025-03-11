package com.example.searcher.controller;

import com.example.searcher.engine.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public String[] search(@RequestParam String query) throws Exception {
        if (query.length() < 3) {
            throw new IllegalArgumentException("Query must be at least 3 characters long.");
        }
        return searchService.search(query);
    }
}
