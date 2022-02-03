package com.github.mnovikov37.cftsort;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Sorter {
    private ParamHandler paramHandler;
    private SortType sortType;
    private DataType dataType;
    private List<FileHandler> files;
    private BufferedWriter outputWriter;
    private List<String> criticalErrorMessages;

    public List<String> getCriticalErrorMessages() { return criticalErrorMessages; }

    public Sorter(ParamHandler paramHandler) {
        files = new ArrayList<>();
        criticalErrorMessages = new ArrayList<>();
        try { // TODO: поменять проверки на входной и выходные файлы местами.
            outputWriter = new BufferedWriter(new FileWriter(paramHandler.getOutputFileName()));
        } catch (IOException e) {
            criticalErrorMessages.add("\"" + paramHandler.getOutputFileName() + "\": "
                    + ErrorMessage.NOT_ACCESS_TO_OUTPUT_FILE.getMessage()
                    + ". Check that the output file is not busy");
        }
        if (outputWriter != null) {
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
                sortType = paramHandler.getSortType();
                dataType = paramHandler.getDataType();
            }
        }
    }

}
