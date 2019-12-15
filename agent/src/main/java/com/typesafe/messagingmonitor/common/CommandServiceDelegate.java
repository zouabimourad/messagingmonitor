package com.typesafe.messagingmonitor.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.typesafe.messagingmonitor.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.DirectProcessor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommandServiceDelegate {

    private final Cache<String, RunningTask<? extends AbstractCommand>> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    private final ThreadPoolTaskExecutor taskExecutor;
    private final TaskScheduler taskScheduler;
    private final JmsTemplate jmsTemplate;
    private final AppProperties appProperties;

    public <COMMAND extends AbstractCommand, EXECUTOR extends CommandExecutor<COMMAND>> List<RunningTaskSummary<COMMAND>> executeCommand(EXECUTOR executor, AgentCommand<COMMAND> agentCommand) {

        AgentCommand<COMMAND> effectiveAgentCommand = buildEffectiveAgentCommand(agentCommand);

        COMMAND command = agentCommand.getCommand();

        List<RunningTaskSummary<COMMAND>> result = Lists.newArrayList();

        int commandsCount = effectiveAgentCommand.getCommand().getCommandsCount();

        for (int i = 0; i < commandsCount; i++) {

            String requestId = UUID.randomUUID().toString();

            String clientId = command.buildEffectiveClientId(requestId, i);

            final DirectProcessor<FlowReport> flux = DirectProcessor.create();

            Future<?> future = taskExecutor.submit(() -> {

                try (BenchmarkFlux benchFlux = new BenchmarkFlux(taskScheduler, agentCommand.getCommand().getSamplingInterval())) {
                    benchFlux.map(benchmarkEvent -> buildFlowReportInstance(benchmarkEvent, requestId)).subscribe(flux);
                    String commandToString = agentCommand.getCommand().toString();
                    try {
                        log.info("Starting command : {}", commandToString);
                        executor.execute(requestId, clientId, effectiveAgentCommand, benchFlux);
                        log.info("Finished command : {}", commandToString);
                    } catch (Exception e) {
                        log.error("Error in command : {}", commandToString, e);
                        benchFlux.onError();
                    }
                }

            });

            RunningTask<COMMAND> runningTask = new RunningTask<>(requestId, clientId, command, LocalDateTime.now(), future, flux);
            runningTask.setStatus(ExecutionStatus.RUNNING);
            cache.put(requestId, runningTask);

            Consumer<ExecutionStatus> onTerminalState = (state) -> {
                runningTask.setStatus(state);
                // remove flux after 30 seconds
                taskScheduler.schedule(() -> cache.invalidate(requestId), Instant.now().plus(10, ChronoUnit.SECONDS));
            };

            flux.subscribe(report -> {
                jmsTemplate.convertAndSend(DestinationConstants.TOPIC_report, report);
                runningTask.setLastReport(report);
            }, error -> onTerminalState.accept(ExecutionStatus.ERROR), () -> onTerminalState.accept(runningTask.getLastReport().getStatus()));

            result.add(runningTask.getSummary());
        }

        return result;
    }

    private FlowReport buildFlowReportInstance(BenchmarkEvent benchmarkEvent, String requestId) {
        FlowReport flowReport = new FlowReport();
        flowReport.setRequestId(requestId);
        flowReport.setEventCount(benchmarkEvent.getEventCount());
        flowReport.setElapsedTime(benchmarkEvent.getElapsedTime());
        flowReport.setRate(benchmarkEvent.getRate());
        flowReport.setAverageRate(benchmarkEvent.getAverageRate());
        flowReport.setStatus(benchmarkEvent.getStatus());
        return flowReport;
    }

    private <COMMAND extends AbstractCommand> AgentCommand<COMMAND> buildEffectiveAgentCommand(AgentCommand<COMMAND> agentCommand) {
        AgentCommand<COMMAND> effectiveAgentCommand;
        if (agentCommand.getBroker().isEmbedded()) {
            BrokerDTO brokerDTO = buildEmbeddedBrokerDTO();
            effectiveAgentCommand = new AgentCommand<>(agentCommand.getCommand(), brokerDTO);
        } else {
            effectiveAgentCommand = agentCommand;
        }
        return effectiveAgentCommand;
    }

    private BrokerDTO buildEmbeddedBrokerDTO() {
        return new BrokerDTO(CommonConstants.EMBEDDED_BROKER_PUBLIC_ID,
                appProperties.getBrokerHost(),
                false,
                appProperties.getBrokerUsername(),
                appProperties.getBrokerPassword(),
                true,
                Protocol.MQTT.getDefaultPort(),
                true,
                Protocol.AMQP.getDefaultPort());
    }

    public List<RunningTaskSummary<? extends AbstractCommand>> getRunningTasks() {
        return cache.asMap().values().stream()
                .map(RunningTask::getSummary)
                .collect(Collectors.toList());
    }

}
