package ru.nsu.usoltsev.manager.model;

public enum Status {
    IN_PROGRESS,
    READY,
    ERROR;

    public static Status forStr(String value) {
        for (var status : Status.values()) {
            if (status.name().equals(value)) {
                return status;
            }
        }
        return ERROR;
    }
}
