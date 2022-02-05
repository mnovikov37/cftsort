package com.github.mnovikov37.cftsort;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сортировщик входных данных. Сортировка происходит автоматически при создании экземпляра класса.
 */
public class Sorter {
    private SortType sortType;                  // Порядок сортировки: по возрастанию либо по убыванию
    private DataType dataType;                  // Тип данных: целые числа либо строки
    private List<FileHandler> files;            // Обработчики входных файлов
    private BufferedWriter outputWriter;        // Интерфейс записи в выходной файл
    private List<String> criticalErrorMessages; // Пул критических ошибок

    public List<String> getCriticalErrorMessages() { return criticalErrorMessages; }

    /**
     * Сравнивает два объекта. Логика сравнения зависит от типа данных и порядка сортировки.
     * @param t1 Первый объект.
     * @param t2 Второй объект
     * @param <T> Тип данных сравниваемых объектов
     * @return Положительное число в случае t1 > t2, отрицательное число в случае t1 < t2, ноль в случае t1 = t2.
     */
    private <T> int compare(T t1, T t2) {
        int result = 0;

        switch (dataType) {
            case INT -> result = ((Integer)t1).compareTo((Integer)t2);
            case STRING -> result = ((String)t1).compareTo((String)t2);
        }
        result *= sortType.getMultiplier(); // В случае сортировки по убыванию результат инвертируется.

        return result;
    }

    /**
     * Вычисляет индекс элемента в массиве, который будет следующим в выходных данных сортировки.
     * В случае сортировки по возрастанию - индекс наименьшего элемента,
     * в случае сортировки по убыванию - индекс наибольшего элемента.
     * @param input Массив входных данных.
     * @param <T> Тип входных данных.
     * @return Индекс следующего элемента, согласно логике сортировки, -1, если его вычислить невозможно.
     */
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

    /**
     * Берёт из входного файла следующий элемент для сортировки,
     * текущий элемент переносит в массив предыдущих элементов -
     * для отслеживания корректности порядка данных во входном файле.
     * @param prev - массив предыдущих элементов - по количеству входных файлов.
     * @param cur - массив текущих элементов - по количеству входных файлов.
     * @param index - номер элемента, который нужно обновить.
     * @param <T> - тип данных для сортировки.
     */
    private <T> void shift(T[] prev, T[] cur, int index) {
        prev[index] = cur[index];
        switch (dataType) {
            case INT -> cur[index] = (T) files.get(index).getNextInt();
            case STRING -> cur[index] = (T) files.get(index).getNextString();
        }
        // В случае, если новый элемент, взятый из файла, нарушает заданный порядок сортировки,
        // он принимается за null, и дальнейшая обработка данного файла прекращается.
        // Об этом сообщается пользователю.
        if (cur[index] != null && compare(cur[index], prev[index]) < 0) {
            cur[index] = null;
            StringBuilder sb = new StringBuilder();
            sb.append(ErrorLevel.WARNING.getPrefix())
                    .append(": \"").append(files.get(index).getFileName()).append("\" line ")
                    .append(files.get(index).getLineNumber())
                    .append(" - invalid order of data in file. The file is excluded from the further sorting");
            System.out.println(sb);
        }
    }

    /**
     * Записывает строку в выходной файл.
     * @param data Данные, которые требуется внести в файл.
     * @param <T> Тип данных, которые требуется внести в файл.
     * @return true в случае успешной записи, false в случае неудачи.
     */
    private <T> boolean writeLineToOutputFile(T data) {
        boolean result = true;
        try {
            outputWriter.write(data.toString() + "\n");
        } catch (IOException e) {
            // В случае невозможности записи в выходной файл, программа закрывается с сообщением об ошибке.
            System.out.println(ErrorLevel.CRITICAL.getPrefix() + ": cannot write next line into output file\n"
                    + e.getMessage() + "\nProgram will be closed");
            result = false;
        }
        return result;
    }

    /**
     * Освобождение ресурсов - закрытие всех обработчиков входных файлов и буфера записи в выходной файл.
     */
    private void close() {
        for (FileHandler handler: files) {
            handler.close();
        }
        try {
            outputWriter.close();
        } catch (IOException e) {
            // При невозможности закрыть буфер записи в выходной файл,
            // пользователю выдаётся предупреждение о том, что выходные данные могут быть сохранены некорректно.
            System.out.println(ErrorLevel.CRITICAL.getPrefix() + ": cannot save output file\n" + e.getMessage()
                    + "\nOutput data may be saved not correctly");
        }
    }

    /**
     * Сортирует данные из входных файлов.
     * @param <T> Тип входных данных.
     */
    private <T> void sort() {
        int inputFilesCount = files.size();
        T[] current = null;     // Массив текущих данных - по количеству файлов. Последний считанный элемент.
        T[] previous = null;    // Массив предыдущих данных - по количеству файлов. Предыдущий считанный элемент.
        switch (dataType) { // Текущий и предыдущий массивы создаются в зависимости от типа входных данных.
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
            nextIndex = calcNextIndex(current); // Вычисляем, элемент какого файла будет следующим в выходных данных.
            if (nextIndex >= 0) {
                shift(previous, current, nextIndex); // Считываем из входного файла следующий элемент.
                if (!writeLineToOutputFile(previous[nextIndex])) { // Попытка записи данных в выходной файл.
                    nextIndex = -1; // В случае ошибки записи в выходной файл, цикл останавливается.
                }
            }
        }
        close(); // По завершению сортировки, освобождаем ресурсы.
    }

    public Sorter(ParamHandler paramHandler) {
        files = new ArrayList<>();
        criticalErrorMessages = new ArrayList<>();
        for (String name : paramHandler.getInputFileNames()) {
            try {
                // По списку имён из обработчика командной строки создаются обработчики входных файлов.
                files.add(new FileHandler(name));
            } catch (FileNotFoundException e) {
                // В случае ошибки при создании обработчика файлов, такой файл пропускается,
                // сортировка продолжается без него.
                System.out.println(ErrorLevel.WARNING.getPrefix() + ": \"" + name + "\": file not found - skipped");
            }
        }
        if (files.isEmpty()) {
            // Если не удалось создать ни одного обработчика входящих файлов,
            // программа завершается с сообщением о критической ошибке.
            criticalErrorMessages.add(ErrorMessage.NO_INPUT_FILE_FOUND.getMessage()
                    + ". Check names of input files");
        } else {
            try {
                outputWriter = new BufferedWriter(new FileWriter(paramHandler.getOutputFileName()));
            } catch (IOException e) {
                // В случае ошибки инициализации буфера записи в выходной файл,
                // программа завершается с сообщением о критической ошибке.
                criticalErrorMessages.add("\"" + paramHandler.getOutputFileName() + "\": "
                        + ErrorMessage.NOT_ACCESS_TO_OUTPUT_FILE.getMessage()
                        + ". Check that the output file can be created and edited\n" + e.getMessage());
            }
            if (outputWriter != null) { // Если никаких ошибок не возникло, запускаем сортировку.
                sortType = paramHandler.getSortType();
                dataType = paramHandler.getDataType();
                sort();
            }
        }
    }

}
