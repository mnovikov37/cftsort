package com.github.mnovikov37.cftsort;

public enum SortType {
    ASC("-a"),
    DESC("-d");

    private String command;

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

    SortType(String command) {
        this.command = command;
    }
}
