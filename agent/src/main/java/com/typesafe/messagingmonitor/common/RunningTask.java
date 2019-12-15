package com.typesafe.messagingmonitor.common;

import com.typesafe.messagingmonitor.AbstractCommand;
import com.typesafe.messagingmonitor.FlowReport;
import com.typesafe.messagingmonitor.RunningTaskSummary;
import lombok.Getter;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.concurrent.Future;


@Getter
public class RunningTask<COMMAND extends AbstractCommand> extends RunningTaskSummary<COMMAND> {

    private Flux<FlowReport> report;
    private Future<?> future;

    public RunningTask(String requestId, String clientId, COMMAND command, LocalDateTime startDate, Future<?> future, Flux<FlowReport> report) {
        super(requestId, clientId, command, startDate);
        this.future = future;
        this.report = report;
    }

    public RunningTaskSummary<COMMAND> getSummary() {
        RunningTaskSummary<COMMAND> commandRunningTaskSummary = new RunningTaskSummary<>(getRequestId(), getClientId(), getCommand(), getStartDate());
        commandRunningTaskSummary.setStatus(getStatus());
        commandRunningTaskSummary.setLastReport(getLastReport());
        return commandRunningTaskSummary;
    }

}
