package com.typesafe.messagingmonitor.producer.executors;

import com.typesafe.messagingmonitor.CancelRunningTasksRequestDTO;
import com.typesafe.messagingmonitor.ProducerCommand;
import com.typesafe.messagingmonitor.common.BenchmarkFlux;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class ProducerBenchmarkContext {

    private ProducerCommand producerCommand;
    private BenchmarkFlux proxy;
    private boolean stopped = false;
    CountDownLatch countDownLatch;

    public ProducerBenchmarkContext(ProducerCommand producerCommand, BenchmarkFlux proxy, Flux<CancelRunningTasksRequestDTO> cancelRunningTasksRequestFlux) {
        this.producerCommand = producerCommand;
        this.proxy = proxy;
        cancelRunningTasksRequestFlux.subscribe(e -> {
            if (countDownLatch != null) {
                while (countDownLatch.getCount() > 0) { //TODO : better way ?
                    countDownLatch.countDown();
                }
            }
            stopped = true;
        });
    }

    public void start(Runnable runnable) {
        proxy.init();
        for (int i = 0; i < producerCommand.getMessagesCount(); i++) {
            if (stopped) {
                break;
            }
            runnable.run();
            proxy.onExecution();
        }
    }

    public <T> void startAsync(Runnable runnable, Flux<T> ackFlux) {
        proxy.init();

        countDownLatch = new CountDownLatch(producerCommand.getMessagesCount());
        ackFlux.subscribe(ack -> {
            countDownLatch.countDown();
            proxy.onExecution();
        });

        for (int i = 0; i < producerCommand.getMessagesCount(); i++) {
            if (stopped) {
                break;
            }
            runnable.run();
        }

        try {
            if (!stopped) {
                countDownLatch.await();
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

}


