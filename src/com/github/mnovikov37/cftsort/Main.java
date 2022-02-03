package com.github.mnovikov37.cftsort;

import java.util.List;

public class Main {

    static void printMessages(ErrorLevel errorLevel, List<String> messages) {
        for (String message: messages) {
            System.out.println(errorLevel.getPrefix() + ": " + message);
        }
        if (errorLevel == ErrorLevel.CRITICAL) {
            System.out.println("Critical errors found. Program will be closed.");
        }
    }

    public static void main(String[] args) {
        ParamHandler paramHandler = new ParamHandler(args);

        if (!paramHandler.getWarningMessages().isEmpty()) {
            printMessages(ErrorLevel.WARNING, paramHandler.getWarningMessages());
        }
        if (paramHandler.getCriticalErrorMessages().isEmpty()) {
            Sorter sorter = new Sorter(paramHandler);
            if (sorter.getCriticalErrorMessages().isEmpty()) {
                System.out.println("Sort is begin");
            } else {
                printMessages(ErrorLevel.CRITICAL, sorter.getCriticalErrorMessages());
            }
        } else {
            printMessages(ErrorLevel.CRITICAL, paramHandler.getCriticalErrorMessages());
        }
    }
}
