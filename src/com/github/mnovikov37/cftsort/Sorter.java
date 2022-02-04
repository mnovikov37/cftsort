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

    private <T> int compare(T t1, T t2) {
        int result = 0;

        switch (dataType) {
            case INT -> result = ((Integer)t1).compareTo((Integer)t2);
            case STRING -> result = ((String)t1).compareTo((String)t2);
        }
        result *= sortType.getMultiplier();

        return result;
    }

    private <T> int calcNextIndex(T[] input) {
        int result = -1;
        int count = input.length;

        T limit = null;
        for (int i = 0; i < count; i++) {
            if (input[i] != null && (limit == null || compare(input[i], limit) <= 0)) {
                limit = input[i];
                result = i;
            }
        }

        return result;
    }

    private <T> void shift(T[] prev, T[] cur, int index) {
        prev[index] = cur[index];
        switch (dataType) {
            case INT -> cur[index] = (T) files.get(index).getNextInt();
            case STRING -> cur[index] = (T) files.get(index).getNextString();
        }
        if (cur[index] != null && compare(cur[index], prev[index]) < 0) {
            cur[index] = null;
            StringBuilder sb = new StringBuilder();
            sb.append("\"").append(files.get(index).getFileName()).append("\" line ")
                    .append(files.get(index).getLineNumber())
                    .append(" - invalid order of data in file. The file is excluded from the further sorting");
            System.out.println(sb);
        }
    }

    private <T> boolean writeLineToOutputFile(T data) {
        boolean result = true;
        try {
            outputWriter.write(data.toString() + "\n");
        } catch (IOException e) {
            System.out.println("Critical error: cannot write next line into output file\n"
                    + e.getMessage() + "\nProgram will be closed");
            result = false;
        }
        return result;
    }

    private void closeWriter() {
        try {
            outputWriter.close();
        } catch (IOException e) {
            System.out.println("Critical error: cannot save output file\n" + e.getMessage()
                    + "\nOutput data may be saved not correctly");
        }
    }

    private <T> void sort() {
        int inputFilesCount = files.size();
        T[] current = null;
        T[] previous = null;
        switch (dataType) {
            case INT -> {
                current = (T[]) new Integer[inputFilesCount];
                previous = (T[]) new Integer[inputFilesCount];
                for (int i = 0; i < inputFilesCount; i++) {
                    previous[i] = (T) files.get(i).getNextInt();
                    current[i] = previous[i];
                }
            }
            case STRING -> {
                current = (T[]) new String[inputFilesCount];
                previous = (T[]) new String[inputFilesCount];
                for (int i = 0; i < inputFilesCount; i++) {
                    previous[i] = (T) files.get(i).getNextString();
                    current[i] = previous[i];
                }
            }
        }

        int nextIndex = 0;
        while (nextIndex >= 0) {
            nextIndex = calcNextIndex(current);
            if (nextIndex >= 0) {
                shift(previous, current, nextIndex);
                if (!writeLineToOutputFile(previous[nextIndex])) {
                    nextIndex = -1;
                }
            }
        }
        closeWriter();
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
                sort();
            }
        }
    }

}
