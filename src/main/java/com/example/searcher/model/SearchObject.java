package com.example.searcher.model;

import java.util.List;

public class SearchObject {
    private String triggerText;
    private String path;
    private List<String> keywords;

    // Default constructor (required for JSON deserialization)
    public SearchObject() {
    }

    // Parameterized constructor
    public SearchObject(String triggerText, String path, List<String> keywords) {
        this.triggerText = triggerText;
        this.path = path;
        this.keywords = keywords;
    }

    // Getters and setters
    public String getTriggerText() {
        return triggerText;
    }

    public void setTriggerText(String triggerText) {
        this.triggerText = triggerText;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "SearchObject{" +
                "trigger_text='" + triggerText + '\'' +
                "path='" + path + '\'' +
                ", keywords=" + keywords +
                '}';
    }
}