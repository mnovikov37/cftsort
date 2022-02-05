package com.github.mnovikov37.cftsort;

/**
 * Порядок сортировки: по возрастанию либо по убыванию.
 */
public enum SortType {
    ASC("-a", 1),
    DESC("-d", -1);

    private String command; // Параметр командной строки, задающий порядок сортировки.
    private int multiplier; // Множитель - используется в логике сортировки.

    public int getMultiplier() { return multiplier; }

    /**
     * Получение порядка сортировки по параметру командной строки.
     * @param command Параметр командной строки.
     * @return Тип сортировки, соответствующий параметру командной строки.
     */
    public static SortType forCommand(String command) {
        SortType result = null;

        for (SortType type: SortType.values()) {
            if (type.command.equals(command)) {
                result = type;
                break;
            }
        }

        return result;
    }

    SortType(String command, int multiplier) {
        this.command = command;
        this.multiplier = multiplier;
    }
}
