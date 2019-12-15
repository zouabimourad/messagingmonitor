package com.typesafe.messagingmonitor;

public enum ExecutionStatus {

    NEW,
    RUNNING,
    COMPLETED,
    ERROR;

    public boolean isTerminal() {
        return this == COMPLETED || this == ERROR;
    }

}
