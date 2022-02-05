package com.github.mnovikov37.cftsort;

import java.util.List;

public class Main {

    /**
     * Выводит на печать список ошибок.
     * @param errorLevel Уровень ошибок в списке: предупреждение либо критическая ошибка. Влияет на сообщение в конце.
     * @param messages Список сообщений об ошибках.
     */
    static void printMessages(ErrorLevel errorLevel, List<String> messages) {
        for (String message: messages) {
            System.out.println(errorLevel.getPrefix() + ": " + message);
        }
        if (errorLevel == ErrorLevel.CRITICAL) {
            System.out.println("Critical errors found. Program will be closed.");
        }
    }

    public static void main(String[] args) {
        // Обработчик командной строки валидирует параметры и преобразует их в данные,
        // необходимые для работы программы.
        ParamHandler paramHandler = new ParamHandler(args);

        // Полученные в ходе работы обработчика командной строки ошибки выводим пользователю.
        if (!paramHandler.getWarningMessages().isEmpty()) {
            printMessages(ErrorLevel.WARNING, paramHandler.getWarningMessages());
        }

        // Если обработчик командной строки не выдал критических ошибок - продолжаем выполнение программы.
        if (paramHandler.getCriticalErrorMessages().isEmpty()) {
            Sorter sorter = new Sorter(paramHandler); // На основе параметров КС создаётся сортировщик.
            if (!sorter.getCriticalErrorMessages().isEmpty()) {
                // Если в сортировщике возникли критическе ошибки - выводим их пользователю.
                // Внутренняя логика сортировщика в таком случае завершит работу программы.
                printMessages(ErrorLevel.CRITICAL, sorter.getCriticalErrorMessages());
            }
        } else { // Если в обработчике командной строки возникли критические ошибки - печатаем их и завершаем работу.
            printMessages(ErrorLevel.CRITICAL, paramHandler.getCriticalErrorMessages());
        }
    }
}
