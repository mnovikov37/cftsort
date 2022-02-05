package com.github.mnovikov37.cftsort;

import java.io.*;

/**
 * Обработчик взаимодействия с входными файлами.
 */
public class FileHandler {
    /**
     * Имя входного файла.
     * Задаётся конструктором.
     */
    private String fileName;
    /**
     * Считыватель данных входного файла.
     * Создаётся в конструкторе на основании fileName
     */
    private BufferedReader reader;
    /**
     * Номер строки в файле.
     * Используется при формировании информации об ошибках.
     */
    private long lineNumber;

    public long getLineNumber() { return lineNumber; }
    public String getFileName() { return fileName; }

    /**
     * Проверяет наличие пробелов в строке.
     * @param s проверяемая строка.
     * @return true, если пробелы обнаружены, false, если нет.
     */
    private boolean containsSpace(String s) {
        boolean result = false;
        for (int i = 0; !result && i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                result = true;
            }
        }
        return result;
    }

    /**
     * Считывает очередную строку в файле.
     * Если считываемая строка валидна - возвращает её,
     * если не валидна - считывает следующую строку, пока не дойдёт до валидной строки,
     * либо до конца файла.
     * Ошибки доступа к файлу обрабатываются так же, как конец файла - файл не участвует в дальнейшей сортировке,
     * но данные, уже взятые из файла прежде этого события, включаются в результат работы программы.
     * Функция информирует пользователя обо всех возникших ошибках с указанием имени файла и строки.
     * @return Следующая строка входного файла.
     */
    public String getNextString() {
        String line = null;
        StringBuilder infoSb = new StringBuilder();
        infoSb.append(ErrorLevel.WARNING.getPrefix()).append(": \"")
                .append(fileName).append("\" line ").append(lineNumber).append(": ");
        try {
            boolean skip;
            do {
                skip = false;
                line = reader.readLine();
                lineNumber++;
                if (line != null) { // Считываемая строка не должна быть null.
                    if (line.length() == 0) { // Строки нулевой длины пропускаются.
                        infoSb.append("empty line - skipped");
                        System.out.println(infoSb);
                        skip = true;
                    } else if (containsSpace(line)){ // Строки, содержащие пробелы, пропускаются - согласно заданию
                        infoSb.append("line with space - skipped");
                        System.out.println(infoSb);
                        skip = true;
                    }
                }
            } while (skip); // Если считываемая строка пропускается - считываем следующую.
        } catch (IOException e) { // При технической ошибке обработка файла завершается с выдачей информации.
            infoSb.append("cannot read from file - technical error. The file is excluded from the further sorting\n")
                    .append(e.getMessage());
            System.out.println(infoSb);
        }
        return line;
    }

    /**
     * Считывает очередное целое число из текстового файла. Одна строка - одно число.
     * В случае, если из входной строки невозможно получить целое число,
     * пользователю выдаётся предупреждение с указанием номера строки во входном файле,
     * и функция пытается считать число из следующей строки,
     * пока не встретит валидную строку, либо конец файла.
     * @return Следующее число из входного файла.
     */
    public Integer getNextInt() {
        Integer result = null;

        boolean skip;
        do {
            skip = false;
            String line = getNextString();
            StringBuilder infoSb = new StringBuilder();
            infoSb.append(ErrorLevel.WARNING.getPrefix())
                    .append(": \"").append(fileName).append("\" line ").append(lineNumber).append(": ");
            if (line != null) {
                try {
                    result = Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    infoSb.append("invalid number format - skipped");
                    System.out.println(infoSb);
                    skip = true;
                }
            }
        } while (skip); // Если считываемое число пропускается - считываем следующее.

        return result;
    }

    /**
     * Освобождение ресурсов - закрытие считывателя из файла.
     */
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                System.out.println(ErrorLevel.WARNING.getPrefix() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Обработчик входного файла создаётся по имени файла.
     * @param fileName Имя входного файла.
     * @throws FileNotFoundException Если файл с заданным именем не найден.
     */
    public FileHandler(String fileName) throws FileNotFoundException {
        this.fileName = fileName;
        FileReader fr = new FileReader(fileName);
        reader = new BufferedReader(fr);
        this.lineNumber = 0;
    }
}
