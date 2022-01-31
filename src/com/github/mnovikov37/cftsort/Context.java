package com.github.mnovikov37.cftsort;

import java.util.List;

public class Context {
    private boolean AscendingSort = true;
    private DataType dataType;
    private String outputFileName;
    private List<String> inputFileNames;

    private final static char MINUS = '-';

    private boolean isOption(String parameter) {
        boolean result = false;
        if (parameter.length() > 0 && parameter.charAt(0) == MINUS) {
            result = true;
        }
        return result;
    }

    public Context(String[] args) {
        if (args.length > 0 && isOption(args[1])) {
            System.out.println(args[1]);
        }
    };
}
