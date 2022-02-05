package com.github.mnovikov37.cftsort;

/**
 * Тип данных, которые требуется отсортировать.
 */
public enum DataType {
    INT("-i"),
    STRING("-s");

    /**
     * Параметр командной строки, соответствующий типу данных.
     */
    private String command;

    /**
     * Получение типа данных по параметру командной строки.
     * @param command Параметр командной строки
     * @return Тип данных, соответствующий параметру командной строки.
     */
    public static DataType forCommand(String command) {
        DataType result = null;

        for (DataType type: DataType.values()) {
            if (type.command.equals(command)) {
                result = type;
                break;
            }
        }

        return result;
    }

    DataType(String command) {
        this.command = command;
    }
}
