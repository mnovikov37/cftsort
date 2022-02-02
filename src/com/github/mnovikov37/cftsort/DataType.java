package com.github.mnovikov37.cftsort;

public enum DataType {
    INT("-i"),
    STRING("-s");

    private String command;

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

    DataType(String command) { this.command = command; }
}
