package com.typesafe.messagingmonitor.producer.executors;

import com.typesafe.messagingmonitor.ProducerCommand;
import com.typesafe.messagingmonitor.Protocol;
import com.typesafe.messagingmonitor.common.CommandExecutor;
import com.typesafe.messagingmonitor.common.CommandExecutorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProducerCommandExecutorFactory implements CommandExecutorFactory<ProducerCommand, CommandExecutor<ProducerCommand>> {

    private final MQTTProducerCommandExecutor mqttConsumerCommandExecutor;

    private final JMSOverAMQPProducerCommandExecutor jmsOverAMQPConsumerCommandExecutor;

    public CommandExecutor<ProducerCommand> getExecutor(Protocol protocol) {

        switch (protocol) {
            case MQTT:
                return mqttConsumerCommandExecutor;
            case AMQP:
                return jmsOverAMQPConsumerCommandExecutor;
            default:
                return null;

        }

    }

}
