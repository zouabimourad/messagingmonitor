package com.typesafe.messagingmonitor.consumer.executors;

import com.typesafe.messagingmonitor.CancelRunningTasksRequestDTO;
import com.typesafe.messagingmonitor.ConsumerCommand;
import com.typesafe.messagingmonitor.common.BenchmarkFlux;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.Optional.ofNullable;

@Slf4j
public class ConsumerBenchmarkContext implements AutoCloseable {

    private ConsumerCommand consumerCommand;
    private CountDownLatch countDownLatch;
    private BenchmarkFlux benchmarkFlux;
    private boolean stopped = false;

    public ConsumerBenchmarkContext(ConsumerCommand consumerCommand, BenchmarkFlux benchmarkFlux, Flux<CancelRunningTasksRequestDTO> cancelRunningTasksRequestFlux) {
        this.consumerCommand = consumerCommand;
        this.countDownLatch = new CountDownLatch(ofNullable(consumerCommand.getMessagesCount()).orElse(1));
        this.benchmarkFlux = benchmarkFlux;
        this.benchmarkFlux.init();
        cancelRunningTasksRequestFlux.subscribe(e -> {
            while (countDownLatch.getCount() > 0) { //TODO : better way ?
                countDownLatch.countDown();
            }
        });
    }

    public void onNewMessage() {
        if (log.isDebugEnabled()) {
            log.debug("New message");
        }
        if (consumerCommand.getMessagesCount() != null) {
            countDownLatch.countDown();
        }
        benchmarkFlux.onExecution();
    }

    @Override
    public void close() {
        try {
            if (consumerCommand.getConsumptionPeriod() != null) {
                countDownLatch.await(consumerCommand.getConsumptionPeriod(), TimeUnit.MINUTES);
            } else {
                countDownLatch.await();
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

}


