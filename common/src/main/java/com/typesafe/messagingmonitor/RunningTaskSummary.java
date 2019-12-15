package com.typesafe.messagingmonitor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
public class RunningTaskSummary<COMMAND extends AbstractCommand> implements Serializable {

    protected final String requestId;

    protected final String clientId;

    protected final COMMAND command;

    protected final LocalDateTime startDate;

    protected ExecutionStatus status;

    protected FlowReport lastReport;

    @JsonCreator
    public RunningTaskSummary(@JsonProperty("requestId") String requestId,
                              @JsonProperty("clientId") String clientId,
                              @JsonProperty("command") COMMAND command,
                              @JsonProperty("startDate") LocalDateTime startDate) {

        this.requestId = requestId;
        this.clientId = clientId;
        this.command = command;
        this.startDate = startDate;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }

    public void setLastReport(FlowReport lastReport) {
        this.lastReport = lastReport;
    }

}
