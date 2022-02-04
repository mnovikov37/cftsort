package com.github.mnovikov37.cftsort;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Sorter {
    private SortType sortType;
    private DataType dataType;
    private List<FileHandler> files;
    private BufferedWriter outputWriter;
    private List<String> criticalErrorMessages;

    public List<String> getCriticalErrorMessages() { return criticalErrorMessages; }

    public void sort() {
        if (dataType == DataType.INT) {
            intSort();
        }
        try {
            outputWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private int compareInt(Integer num1, Integer num2) {
        int result = 0;

        if (num1 > num2) {
            result = 1;
        }
        if (num1 < num2) {
            result = -1;
        }
        result *= sortType.getMultiplier();

        return result;
    }

    private int calcNextIndex(Integer[] input) {
        int result = -1;
        int count = input.length;

        Integer limit = sortType == SortType.ASC ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        for (int i = 0; i < count; i++) {
            if (input[i] != null && compareInt(input[i], limit) < 0) {
                limit = input[i];
                result = i;
            }
        }

        return result;
    }

    private void shift(Integer[] prev, Integer[] cur, int index) {
        prev[index] = cur[index];
        cur[index] = files.get(index).getNextInt();
        if (cur[index] != null && compareInt(cur[index], prev[index]) < 0) {
            cur[index] = null;
            StringBuilder sb = new StringBuilder();
            sb.append("\"").append(files.get(index).getFileName()).append("\" line ")
                    .append(files.get(index).getLineNumber())
                    .append(" - invalid order of data in file. The file is excluded  from further sorting");
            System.out.println(sb);
        }
    }

    private void intSort() {
        int inputFilesCount = files.size();
        Integer[] currentValue = new Integer[inputFilesCount];
        Integer[] previousValue = new Integer[inputFilesCount];
        for (int i = 0; i < inputFilesCount; i++) {
            previousValue[i] = Integer.MIN_VALUE;
            currentValue[i] = files.get(i).getNextInt();
        }
        int nextIndex = 0;
        while (nextIndex >= 0) {
            nextIndex = calcNextIndex(currentValue);
//            System.out.println("NI = " + nextIndex);
            if (nextIndex >= 0) {
//                System.out.println("VAL = " + currentValue[nextIndex]);
                shift(previousValue, currentValue, nextIndex);
                try {
                    outputWriter.write(previousValue[nextIndex].toString() + "\n");
                } catch (IOException e) {
                    System.out.println("Critical error: cannot write next line into output file\n"
                            + e.getMessage() + "\nProgram will be closed");
                    nextIndex = -1;
                }
            }
        }
    }

    public Sorter(ParamHandler paramHandler) {
        files = new ArrayList<>();
        criticalErrorMessages = new ArrayList<>();
        for (String name : paramHandler.getInputFileNames()) {
            try {
                files.add(new FileHandler(name));
            } catch (FileNotFoundException e) {
                System.out.println("\"" + name + "\": file not found - skipped");
            }
        }
        if (files.isEmpty()) {
            criticalErrorMessages.add(ErrorMessage.NO_INPUT_FILE_FOUND.getMessage()
                    + ". Check names of input files");
        } else {
            try {
                outputWriter = new BufferedWriter(new FileWriter(paramHandler.getOutputFileName()));
            } catch (IOException e) {
                criticalErrorMessages.add("\"" + paramHandler.getOutputFileName() + "\": "
                        + ErrorMessage.NOT_ACCESS_TO_OUTPUT_FILE.getMessage()
                        + ". Check that the output file can be created and edited\n" + e.getMessage());
            }
            if (outputWriter != null) {
                sortType = paramHandler.getSortType();
                dataType = paramHandler.getDataType();
            }
        }
    }

}
