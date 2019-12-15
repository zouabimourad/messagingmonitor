package com.typesafe.messagingmonitor.common;

import com.typesafe.messagingmonitor.ExecutionStatus;
import org.springframework.scheduling.TaskScheduler;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;

import java.util.concurrent.ScheduledFuture;

public class BenchmarkFlux extends Flux<BenchmarkEvent> implements AutoCloseable {

    private final TaskScheduler scheduler;
    private final long samplingInterval;
    private long executionCount = 0;
    private int intervalExecutionCount;
    private long firstSampleTimestamp = 0;
    private long lastSampleTimestamp = 0;
    private boolean error = false;

    private ScheduledFuture<?> scheduledFuture;
    private final DirectProcessor<BenchmarkEvent> flux = DirectProcessor.create();

    public BenchmarkFlux(TaskScheduler scheduler, long samplingInterval) {
        this.scheduler = scheduler;
        this.samplingInterval = samplingInterval;
    }

    public void init() {
        this.scheduledFuture = scheduler.scheduleAtFixedRate(() -> sample(System.currentTimeMillis(), ExecutionStatus.RUNNING), samplingInterval);
    }

    public void onExecution() {
        executionCount++;
        intervalExecutionCount++;
    }

    public void onError() {
        this.error = true;
        onTerminalState(ExecutionStatus.ERROR);
    }

    private void sample(long timestamp, ExecutionStatus executionStatus) {

        long elapsedTime = timestamp - lastSampleTimestamp;
        long totalElapsedTime = timestamp - firstSampleTimestamp;

        BenchmarkEvent event = new BenchmarkEvent(
                executionCount,
                firstSampleTimestamp != 0 ? totalElapsedTime : 0,
                elapsedTime != 0 ? intervalExecutionCount * 1000 / elapsedTime : 0,
                totalElapsedTime != 0 ? executionCount * 1000 / totalElapsedTime : 0,
                executionStatus);

        flux.onNext(event);

        intervalExecutionCount = 0;
        lastSampleTimestamp = timestamp;

        if (firstSampleTimestamp == 0) {
            firstSampleTimestamp = timestamp;
        }

        lastSampleTimestamp = timestamp;
    }

    @Override
    public void subscribe(CoreSubscriber actual) {
        flux.subscribe(actual);
    }

    private void onTerminalState(ExecutionStatus status) {
        if (!status.isTerminal()) {
            throw new IllegalArgumentException();
        }
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        sample(System.currentTimeMillis(), status);
    }

    @Override
    public void close() {
        if (!error) {
            onTerminalState(ExecutionStatus.COMPLETED);
        }
        if (!flux.isTerminated()) {
            flux.onComplete();
        }
    }
}