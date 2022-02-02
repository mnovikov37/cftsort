package com.github.mnovikov37.cftsort;

import java.util.ArrayList;
import java.util.List;

public class ParamHandler {
    private SortType sortType;
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

    public ParamHandler(String[] args) {
        inputFileNames = new ArrayList<>();
        for (String arg : args) {
            if (isOption(arg)) {
                switch (arg) {
                    case "-a", "-d" -> {
                        if (sortType == null) {
                            sortType = SortType.forCommand(arg);
                        } else {
                            System.out.println("ERROR: sort type many params");
                        }
                    }
                    case "-i", "-s" -> {
                        if (dataType == null) {
                            dataType = DataType.forCommand(arg);
                        } else {
                            System.out.println("ERROR: data type many params");
                        }
                    }
                    default -> System.out.println("ERROR: unknown param \"" + arg + "\"");
                }
            } else if (outputFileName == null) {
                outputFileName = arg;
            } else {
                if (!(arg.equals(outputFileName))) {
                    if (inputFileNames.contains(arg)) {
                        System.out.println("WARNING: duplicate inputFileName \"" + arg + "\"");
                    }
                    inputFileNames.add(arg);
                } else {
                    System.out.println("WARNING: inputFileName equals outputFileName. Invalid outputFileName excluded from list");
                    // Пояснить в документации, что подразумевается допустимым
                    // использование одного и того же входного файла более одного раза.
                }
            }
        }
        System.out.println("sortType = " + sortType);
        System.out.println("dataType = " + dataType);
        System.out.println("outputFileName = " + outputFileName);
        System.out.println("inputFileName = " + inputFileNames);
    }
}
