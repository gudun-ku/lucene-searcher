package com.example.searcher.engine;

public interface SearchService {
    String[] search(String queryString, String lang) throws Exception;
}
