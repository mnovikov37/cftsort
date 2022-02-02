package com.github.mnovikov37.cftsort;

public enum ErrorMessage {

    DUPLICATE_SORT_TYPE_PARAM("More then one params of sort type"),
    DUPLICATE_DATA_TYPE_PARAM("More then one params of data type"),
    UNKNOWN_PARAM("Unknown param"),
    DUPLICATE_INPUT_FILE_NAME("More then one input file name"),
    INPUT_FILE_NAME_EQUALS_OUTPUT_FILE_NAME("Input file names contains name of output file");

    private String message;

    public String getMessage() {
        return message;
    }

    ErrorMessage(String message) {
        this.message = message;
    }
}
