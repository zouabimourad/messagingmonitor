package com.typesafe.messagingmonitor.producer.executors;

import com.typesafe.messagingmonitor.AgentCommand;
import com.typesafe.messagingmonitor.BrokerDTO;
import com.typesafe.messagingmonitor.CancelRunningTasksRequestDTO;
import com.typesafe.messagingmonitor.ProducerCommand;
import com.typesafe.messagingmonitor.common.BenchmarkFlux;
import com.typesafe.messagingmonitor.common.CommandExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;

import javax.jms.*;

import static com.typesafe.messagingmonitor.common.jmsoveramqp.JMSOverAMQPHelper.buildDestination;
import static com.typesafe.messagingmonitor.common.jmsoveramqp.JMSOverAMQPHelper.buildJMSContext;

@RequiredArgsConstructor
@Slf4j
@Service
public class JMSOverAMQPProducerCommandExecutor implements CommandExecutor<ProducerCommand> {

    private final Flux<CancelRunningTasksRequestDTO> cancelRunningTasksRequestFlux;

    @Override
    public void execute(String requestId, String clientId, AgentCommand<ProducerCommand> agentCommand, BenchmarkFlux benchmarkFlux) {

        ProducerCommand command = agentCommand.getCommand();
        BrokerDTO brokerDTO = agentCommand.getBroker();

        try (JMSContext context = buildJMSContext(brokerDTO.getHost(), brokerDTO.isSecured(), brokerDTO.getAmqpPort(), brokerDTO.getUsername(), brokerDTO.getPassword(), clientId)) {

            JMSProducer jmsProducer = context.createProducer().setDeliveryMode(command.getJmsDeliveryMode().getCode());

            Destination destination = buildDestination(context, command.getDestination(), command.getJmsDestinationType());
            ProducerBenchmarkContext producerBenchmarkContext = new ProducerBenchmarkContext(command, benchmarkFlux, cancelRunningTasksRequestFlux);

            if (command.getJmsTimeToLive() != null) {
                jmsProducer.setTimeToLive(command.getJmsTimeToLive());
            }

            final String message = command.buildEffectiveMessage();

            if (command.isAsync()) {

                DirectProcessor<Message> ackFlux = DirectProcessor.create();
                jmsProducer = jmsProducer.setAsync(new CompletionListener() {
                    @Override
                    public void onCompletion(Message message) {
                        if (log.isDebugEnabled()) {
                            log.info("Message arrived");
                        }
                        ackFlux.onNext(message);
                    }

                    @Override
                    public void onException(Message message, Exception exception) {
                        log.error(exception.getMessage(), exception);
                        ackFlux.onNext(message);
                    }
                });

                JMSProducer fJMSProducer = jmsProducer;
                producerBenchmarkContext.startAsync(() -> fJMSProducer.send(destination, message), ackFlux);

            } else {

                JMSProducer fJMSProducer = jmsProducer;
                producerBenchmarkContext.start(() -> fJMSProducer.send(destination, message));

            }

        }

    }

}
