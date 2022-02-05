package com.github.mnovikov37.cftsort;

/**
 * Уровень ошибки: предупреждение либо критическая ошибка.
 * В случае предупреждения программа продолжает выполнение, но в её поведение вносятся корректировки -
 * результат может отличаться от ожидаемого пользователем.
 * В случае критической ошибки программа завершается.
 * В обоих случаях пользователю указывается на место ошибки.
 */
public enum ErrorLevel {
    WARNING("Warning"),
    CRITICAL("Critical error");

    /**
     * Строка, с которой будет начинаться сообщение об ошибке.
     */
    private String prefix;

    public String getPrefix() { return prefix; }

    ErrorLevel(String prefix) {
        this.prefix = prefix;
    }
}
