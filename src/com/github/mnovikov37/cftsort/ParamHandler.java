package com.github.mnovikov37.cftsort;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик командной строки. Валидирует параметры командной строки и создаёт на их основе данные,
 * необходимые для работы программы.
 */
public class ParamHandler {
    private final static char PARAMETER_FLAG = '-';         // признак команды
    private final static String SORT_TYPE_ASC = "-a";       // команда: сортировка по возрастанию
    private final static String SORT_TYPE_DESC = "-d";      // команда: сортировка по убыванию
    private final static String DATA_TYPE_INT = "-i";       // команда: тип данных - целые числа
    private final static String DATA_TYPE_STRING = "-s";    // команда: тип данных - строки

    private SortType sortType;                      // Тип сортировки: по возрастанию либо по убыванию
    private DataType dataType;                      // Тип данных: целые числа либо строки
    private String outputFileName;                  // Имя выходного файла
    private List<String> inputFileNames;            // Имена входных файлов
    private List<String> warningMessages;           // Сообщения об ошибках уровня "предупреждение"
    private List<String> criticalErrorMessages;     // Сообщения об ошибках уровня "критическая ошибка"

    public SortType getSortType() { return sortType; }
    public DataType getDataType() { return dataType; }
    public String getOutputFileName() { return outputFileName; }
    public List<String> getInputFileNames() { return inputFileNames; }
    public List<String> getWarningMessages() { return warningMessages; }
    public List<String> getCriticalErrorMessages() { return criticalErrorMessages; }

    /**
     * Проверяет, является ли строка командой. То есть начинается ли с символа PARAMETER_FLAG.
     * @param parameter Параметр командной строки.
     * @return true, если параметр команда, false, если параметр не является командой.
     */
    private boolean isOption(String parameter) {
        boolean result = false;
        if (parameter.length() > 0 && parameter.charAt(0) == PARAMETER_FLAG) {
            result = true;
        }
        return result;
    }

    /**
     * Добавляет сообщение к списку сообщений.
     * @param messages Список сообщений.
     * @param message Сообщение.
     */
    private void addToMessages(List<String> messages, String message) {
        if (!messages.contains(message)) {
            messages.add(message);
        }
    }

    /**
     * Конструктор класса сразу запускает обработку командной строки
     * @param args Аргументы командной строки
     */
    public ParamHandler(String[] args) {
        inputFileNames = new ArrayList<>();
        warningMessages = new ArrayList<>();
        criticalErrorMessages = new ArrayList<>();
        for (String arg : args) {
            StringBuilder errorMessageBuilder = new StringBuilder();
            // Обработка команд
            if (isOption(arg)) {
                switch (arg) {
                    case SORT_TYPE_ASC, SORT_TYPE_DESC -> {
                        if (sortType == null) {
                            sortType = SortType.forCommand(arg);
                        } else { // Если тип сортировки указан более одного раза, берётся тот, что был указан первым.
                            errorMessageBuilder.append(ErrorMessage.DUPLICATE_SORT_TYPE_PARAM.getMessage())
                                    .append(". Sort = ").append(sortType)
                                    .append(" according to the first parameter");
                        }
                    }
                    case DATA_TYPE_INT, DATA_TYPE_STRING -> {
                        if (dataType == null) {
                            dataType = DataType.forCommand(arg);
                        } else { // Если тип данных указан более одного раза, берётся тот, что был указан первым.
                            errorMessageBuilder.append(ErrorMessage.DUPLICATE_DATA_TYPE_PARAM.getMessage())
                                    .append(". Data type = ").append(dataType)
                                    .append(" according to the first parameter");
                        }
                    }
                    default -> { // Неизвестные команды игнорируются.
                        errorMessageBuilder.append(ErrorMessage.UNKNOWN_PARAM.getMessage())
                                .append(" \"").append(arg).append("\" - will be ignored");
                    }
                }
            } else if (outputFileName == null) { // Обработка имени выходного файла.
                outputFileName = arg;
            } else { // Если имя выходного файла уже есть, далее следуют имена входных файлов.
                if (!(arg.equals(outputFileName))) {
                    if (inputFileNames.contains(arg)) {
                        // Если имя выходного файла указано более одного раза, то такой файл включается в сортировку
                        // столько раз, сколько он был указан - так, как будто это несколько разных файлов.
                        // Об этом сообщается пользователю.
                        errorMessageBuilder.append(ErrorMessage.DUPLICATE_INPUT_FILE_NAME.getMessage())
                                .append(" \"").append(arg)
                                .append("\" - will used many times");
                    }
                    inputFileNames.add(arg);
                } else {
                    // Если список входных файлов содержит имя выходного файла, то такой файл исключается
                    // из списка входных файлов, программа выполняется. Об этом сообщается пользователю.
                    errorMessageBuilder.append(ErrorMessage.INPUT_FILE_NAME_EQUALS_OUTPUT_FILE_NAME.getMessage())
                                    .append(" - such files have been removed from the list of input files");
                }
            }
            if (errorMessageBuilder.length() > 0) {
                addToMessages(warningMessages, errorMessageBuilder.toString()); // Обнаруженные ошибки заносим в список.
            }
        }

        if (sortType == null) {
            sortType = SortType.ASC; // По умолчанию порядок сортировки - по возрастанию.
        }
        // Далее следуют обязательные параметры, при отсутствии которых выполнение программы завершается,
        // пользователю выдаётся сообщение о критических ошибках.
        if (dataType == null) {
            addToMessages(criticalErrorMessages, ErrorMessage.DATA_TYPE_NOT_SPECIFIED.getMessage());
        }
        if (outputFileName == null) {
            addToMessages(criticalErrorMessages, ErrorMessage.OUTPUT_FILE_NOT_SPECIFIED.getMessage());
        }
        if (inputFileNames.isEmpty()) {
            addToMessages(criticalErrorMessages, ErrorMessage.INPUT_FILES_NOT_SPECIFIED.getMessage());
        }
    }
}
