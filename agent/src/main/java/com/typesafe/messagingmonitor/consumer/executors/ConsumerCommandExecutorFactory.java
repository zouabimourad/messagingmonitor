package com.typesafe.messagingmonitor.consumer.executors;

import com.typesafe.messagingmonitor.ConsumerCommand;
import com.typesafe.messagingmonitor.Protocol;
import com.typesafe.messagingmonitor.common.CommandExecutor;
import com.typesafe.messagingmonitor.common.CommandExecutorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsumerCommandExecutorFactory implements CommandExecutorFactory<ConsumerCommand,CommandExecutor<ConsumerCommand>> {

    private final MQTTConsumerCommandExecutor mqttConsumerCommandExecutor;

    private final JMSOverAMQPConsumerCommandExecutor jmsOverAMQPConsumerCommandExecutor;

    public CommandExecutor<ConsumerCommand> getExecutor(Protocol protocol) {
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
