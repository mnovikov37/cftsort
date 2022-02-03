package com.github.mnovikov37.cftsort;

public enum ErrorLevel {
    WARNING("Warning"),
    CRITICAL("Critical error");

    private String prefix;

    public String getPrefix() { return prefix; }

    ErrorLevel(String prefix) {
        this.prefix = prefix;
    }
}
