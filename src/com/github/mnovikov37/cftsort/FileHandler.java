package com.github.mnovikov37.cftsort;

import java.io.*;

public class FileHandler {
    private String fileName;
    private BufferedReader reader;
    private long lineNumber;

    public long getLineNumber() { return lineNumber; }
    public String getFileName() { return fileName; }

    private boolean containsSpace(String s) {
        boolean result = false;
        for (int i = 0; !result && i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                result = true;
            }
        }
        return result;
    }

    public String getNextString() {
        String line = null;
        try {
            boolean skip;
            do {
                skip = false;
                line = reader.readLine();
                lineNumber++;
                StringBuilder infoSb = new StringBuilder();
                infoSb.append("\"").append(fileName).append("\" line ").append(lineNumber).append(": ");
                if (line != null) {
                    if (line.length() == 0) {
                        infoSb.append("empty line - skipped");
                        System.out.println(infoSb);
                        skip = true;
                    } else if (containsSpace(line)){
                        infoSb.append("line with space - skipped");
                        System.out.println(infoSb);
                        skip = true;
                    }
                }
            } while (skip);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public Integer getNextInt() {
        Integer result = null;

        boolean skip;
        do {
            skip = false;
            String line = getNextString();
            StringBuilder infoSb = new StringBuilder();
            infoSb.append("\"").append(fileName).append("\" line ").append(lineNumber).append(": ");
            if (line != null) {
                try {
                    result = Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    infoSb.append("invalid number format - skipped");
                    System.out.println(infoSb);
                    skip = true;
                }
            }
        } while (skip);

        return result;
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileHandler(String fileName) throws FileNotFoundException {
        this.fileName = fileName;
        FileReader fr = new FileReader(fileName);
        reader = new BufferedReader(fr);
        this.lineNumber = 0;
    }
}
