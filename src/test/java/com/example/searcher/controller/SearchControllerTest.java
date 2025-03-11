package com.example.searcher.controller;

import com.example.searcher.engine.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class) // Load only the web layer (controller)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc for simulating HTTP requests

    @MockitoBean
    private SearchService searchService; // Mock the SearchService dependency

    @Test
    void testSearch_Success() throws Exception {
        // Mock the behavior of SearchService
        when(searchService.search("java", "en")).thenReturn(new String[]{
                "Java is a powerful programming language",
                "Spring Boot makes Java development easier"
        });

        // Perform the GET request and verify the response
        mockMvc.perform(get("/search")
                        .param("query", "java")
                        .param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Java is a powerful programming language"))
                .andExpect(jsonPath("$[1]").value("Spring Boot makes Java development easier"));
    }

    @Test
    void testSearch_DefaultLanguage() throws Exception {
        // Mock the behavior of SearchService
        when(searchService.search("docker", "en")).thenReturn(new String[]{
                "Docker simplifies containerization"
        });

        // Perform the GET request without the "lang" parameter (defaults to "en")
        mockMvc.perform(get("/search")
                        .param("query", "docker"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Docker simplifies containerization"));
    }

    @Test
    void testSearch_QueryTooShort() throws Exception {
        // Perform the GET request with a query that's too short
        mockMvc.perform(get("/search")
                        .param("query", "ab"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Query must be at least 3 characters long."));
    }

    @Test
    void testSearch_UnsupportedLanguage() throws Exception {
        // Mock the behavior of SearchService to throw an exception for unsupported language
        when(searchService.search("test", "fr")).thenThrow(new IllegalArgumentException("Unsupported language: fr"));

        // Perform the GET request with an unsupported language
        mockMvc.perform(get("/search")
                        .param("query", "test")
                        .param("lang", "fr"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unsupported language: fr"));
    }

    @Test
    void testSearch_InternalServerError() throws Exception {
        // Mock the behavior of SearchService to throw an unexpected exception
        when(searchService.search("error", "en")).thenThrow(new RuntimeException("Internal server error"));

        // Perform the GET request and verify the response
        mockMvc.perform(get("/search")
                        .param("query", "error")
                        .param("lang", "en"))
                .andExpect(status().isInternalServerError());
    }
}