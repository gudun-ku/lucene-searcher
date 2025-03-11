package com.example.searcher.model;

import java.util.List;

public class Phrase {
    private String phrase;
    private List<String> keywords;

    // Default constructor (required for JSON deserialization)
    public Phrase() {}

    // Parameterized constructor
    public Phrase(String phrase, List<String> keywords) {
        this.phrase = phrase;
        this.keywords = keywords;
    }

    // Getters and setters
    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public String toString() {
        return "Phrase{" +
                "phrase='" + phrase + '\'' +
                ", keywords=" + keywords +
                '}';
    }
}