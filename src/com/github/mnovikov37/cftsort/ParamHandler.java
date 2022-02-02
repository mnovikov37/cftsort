package com.github.mnovikov37.cftsort;

import java.util.ArrayList;
import java.util.List;

public class ParamHandler {
    private final static char MINUS = '-';
    private final static String SORT_TYPE_ASC = "-a";
    private final static String SORT_TYPE_DESC = "-d";
    private final static String DATA_TYPE_INT = "-i";
    private final static String DATA_TYPE_STRING = "-s";

    private SortType sortType;
    private DataType dataType;
    private String outputFileName;
    private List<String> inputFileNames;
    private List<String> warningMessages;

    private boolean isOption(String parameter) {
        boolean result = false;
        if (parameter.length() > 0 && parameter.charAt(0) == MINUS) {
            result = true;
        }
        return result;
    }

    private void addToMessages(List<String> messages, String message) {
        if (!messages.contains(message)) {
            messages.add(message);
        }
    }

    public ParamHandler(String[] args) {
        inputFileNames = new ArrayList<>();
        warningMessages = new ArrayList<>();
        for (String arg : args) {
            StringBuilder errorMessageBuilder = new StringBuilder();
            if (isOption(arg)) {
                switch (arg) {
                    case SORT_TYPE_ASC, SORT_TYPE_DESC -> {
                        if (sortType == null) {
                            sortType = SortType.forCommand(arg);
                        } else {
                            errorMessageBuilder.append(ErrorMessage.DUPLICATE_SORT_TYPE_PARAM.getMessage())
                                    .append(". Sort = ").append(sortType)
                                    .append(" according to the first parameter");
                        }
                    }
                    case DATA_TYPE_INT, DATA_TYPE_STRING -> {
                        if (dataType == null) {
                            dataType = DataType.forCommand(arg);
                        } else {
                            errorMessageBuilder.append(ErrorMessage.DUPLICATE_DATA_TYPE_PARAM.getMessage())
                                    .append(". Data type = ").append(dataType)
                                    .append(" according to the first parameter");
                        }
                    }
                    default -> {
                        errorMessageBuilder.append(ErrorMessage.UNKNOWN_PARAM.getMessage())
                                .append(" \"").append(arg).append("\" - will be ignored");
                    }
                }
            } else if (outputFileName == null) {
                outputFileName = arg;
            } else {
                if (!(arg.equals(outputFileName))) {
                    if (inputFileNames.contains(arg)) {
                        errorMessageBuilder.append(ErrorMessage.DUPLICATE_INPUT_FILE_NAME.getMessage())
                                .append(" \"").append(arg)
                                .append("\" - will used many times");
                        // Пояснить в документации, что подразумевается допустимым
                        // использование одного и того же входного файла более одного раза.
                    }
                    inputFileNames.add(arg);
                } else {
                    errorMessageBuilder.append(ErrorMessage.INPUT_FILE_NAME_EQUALS_OUTPUT_FILE_NAME.getMessage())
                                    .append(" - such files will be removed from the list of input files");
                }
            }
            if (errorMessageBuilder.length() > 0) {
                addToMessages(warningMessages, errorMessageBuilder.toString());
            }
        }
        for (String message: warningMessages) {
            System.out.println(message);
        }
        System.out.println("sortType = " + sortType);
        System.out.println("dataType = " + dataType);
        System.out.println("outputFileName = " + outputFileName);
        System.out.println("inputFileName = " + inputFileNames);
    }
}
