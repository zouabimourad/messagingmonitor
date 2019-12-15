package com.typesafe.messagingmonitor;

import com.typesafe.messagingmonitor.common.CommandServiceDelegate;
import com.typesafe.messagingmonitor.consumer.ConsumerService;
import com.typesafe.messagingmonitor.producer.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventsListener {

    private final ConsumerService consumerService;

    private final ProducerService producerService;

    private final JmsTemplate jmsTemplate;

    private final Flux<RunningTasksRequestDTO> runningTasksRequestFlux;

    private final CommandServiceDelegate commandServiceDelegate;

    @PostConstruct
    public void init() {
        runningTasksRequestFlux.subscribe(this::onRunningTasksRequest);
    }

    @JmsListener(destination = "#{'mm/agent/' + appProperties.identifier + '/consumerCommand'}", containerFactory = "jmsListenerContainerFactory")
    public List<RunningTaskSummary<ConsumerCommand>> receiveConsumerCommand(@Payload AgentCommand<ConsumerCommand> consumerCommand) {
        return consumerService.executeCommand(consumerCommand);
    }

    @JmsListener(destination = "#{'mm/agent/' + appProperties.identifier + '/producerCommand'}", containerFactory = "jmsListenerContainerFactory")
    public List<RunningTaskSummary<ProducerCommand>> receiveProducerCommand(@Payload AgentCommand<ProducerCommand> producerCommand) {
        return producerService.executeCommand(producerCommand);
    }

    public void onRunningTasksRequest(RunningTasksRequestDTO runningTasksRequest) {
        jmsTemplate.convertAndSend(DestinationConstants.TOPIC_agent_runningTasks, commandServiceDelegate.getRunningTasks());
    }

}
