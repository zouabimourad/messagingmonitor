package com.typesafe.messagingmonitor.consumer.executors;

import com.typesafe.messagingmonitor.*;
import com.typesafe.messagingmonitor.common.BenchmarkFlux;
import com.typesafe.messagingmonitor.common.CommandExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.Topic;

import static com.typesafe.messagingmonitor.common.jmsoveramqp.JMSOverAMQPHelper.buildJMSContext;



@RequiredArgsConstructor
@Slf4j
@Service
public class JMSOverAMQPConsumerCommandExecutor implements CommandExecutor<ConsumerCommand> {

    private final Flux<CancelRunningTasksRequestDTO> cancelRunningTasksRequestFlux;

    @Override
    public void execute(String requestId, String clientId, AgentCommand<ConsumerCommand> agentCommand, BenchmarkFlux benchmarkFlux) {

        ConsumerCommand command = agentCommand.getCommand();
        BrokerDTO brokerDTO = agentCommand.getBroker();

        try (JMSContext context = buildJMSContext(brokerDTO.getHost(), brokerDTO.isSecured(), brokerDTO.getAmqpPort(), brokerDTO.getUsername(), brokerDTO.getPassword(), clientId)) {

            JMSConsumer consumer;

            if (command.getJmsDestinationType() == DestinationType.QUEUE) {

                Queue queue = context.createQueue(command.getDestination());
                consumer = context.createConsumer(queue);

            } else {

                Topic topic = context.createTopic(command.getDestination());

                if (command.isJmsDurableConsumer()) {
                    if (command.isJmsSharedConsumer()) {
                        consumer = context.createSharedDurableConsumer(topic, command.getJmsConsumerName());
                    } else {
                        consumer = context.createDurableConsumer(topic, command.getJmsConsumerName());
                    }
                } else {
                    if (command.isJmsSharedConsumer()) {
                        consumer = context.createSharedConsumer(topic, command.getJmsConsumerName());
                    } else {
                        consumer = context.createConsumer(topic);
                    }
                }

            }

            try (ConsumerBenchmarkContext benchmarkContext = new ConsumerBenchmarkContext(command, benchmarkFlux, cancelRunningTasksRequestFlux)) {
                consumer.setMessageListener(message -> benchmarkContext.onNewMessage());
            }

        }

    }

}
