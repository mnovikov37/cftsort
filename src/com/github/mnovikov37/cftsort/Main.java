package com.github.mnovikov37.cftsort;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        ParamHandler paramHandler = new ParamHandler(args);

        List<String> criticalErrorMessages = paramHandler.getCriticalErrorMessages();
        List<String> warningMessages = paramHandler.getWarningMessages();
        for (String message: warningMessages) {
            System.out.println("Warning: " + message);
        }
        for (String message: criticalErrorMessages) {
            System.out.println("Critical error: " + message);
        }

        if (criticalErrorMessages.isEmpty()) {
            System.out.println("Типа выполняем программу");
        } else {
            System.out.println("Critical errors found. Program will be closed.");
        }
    }
}
