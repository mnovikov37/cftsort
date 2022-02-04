package com.github.mnovikov37.cftsort;

public enum SortType {
    ASC("-a", 1),
    DESC("-d", -1);

    private String command;
    private int multiplier;

    public int getMultiplier() { return multiplier; }

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
