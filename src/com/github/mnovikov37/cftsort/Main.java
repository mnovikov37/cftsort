package com.github.mnovikov37.cftsort;

import java.io.FileNotFoundException;
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

        try {
            FileHandler stringFileHandler = new FileHandler("1.txt", DataType.STRING, SortType.ASC);
            FileHandler integerFileHandler = new FileHandler("2.txt", DataType.INT, SortType.ASC);
            String s = stringFileHandler.getNextString();
            Integer i = integerFileHandler.getNextInt();
            while (s != null) {
                System.out.println(s);
                s = stringFileHandler.getNextString();
            }
            while (i != null) {
                System.out.println(i);
                i = integerFileHandler.getNextInt();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
