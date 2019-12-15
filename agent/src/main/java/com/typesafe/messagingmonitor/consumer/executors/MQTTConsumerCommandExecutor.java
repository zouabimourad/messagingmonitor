package com.typesafe.messagingmonitor.consumer.executors;

import com.typesafe.messagingmonitor.AgentCommand;
import com.typesafe.messagingmonitor.BrokerDTO;
import com.typesafe.messagingmonitor.CancelRunningTasksRequestDTO;
import com.typesafe.messagingmonitor.ConsumerCommand;
import com.typesafe.messagingmonitor.common.BenchmarkFlux;
import com.typesafe.messagingmonitor.common.CommandExecutor;
import com.typesafe.messagingmonitor.common.mqtt.MqttClientAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static com.typesafe.messagingmonitor.common.mqtt.MQTTHelper.buildMqttClientWrapper;
import static com.typesafe.messagingmonitor.common.mqtt.MQTTHelper.buildOptions;

@Slf4j
@Service
@RequiredArgsConstructor
public class MQTTConsumerCommandExecutor implements CommandExecutor<ConsumerCommand> {

    private final RetryTemplate retryTemplate;

    private final Flux<CancelRunningTasksRequestDTO> cancelRunningTasksRequestFlux;

    @Override
    public void execute(String requestId, String clientId, AgentCommand<ConsumerCommand> agentCommand, BenchmarkFlux benchmarkFlux) throws MqttException {

        ConsumerCommand command = agentCommand.getCommand();
        BrokerDTO brokerDTO = agentCommand.getBroker();

        try (MqttClientAdapter mqttClient = buildMqttClientWrapper(false, brokerDTO.getHost(), brokerDTO.isSecured(), brokerDTO.getMqttPort(), clientId)) {

            retryTemplate.execute((RetryCallback<Void, RuntimeException>) context -> {
                try {
                    mqttClient.connect(buildOptions(brokerDTO.getUsername(), brokerDTO.getPassword()));
                } catch (Exception e) {
                    log.error("failed to connect to MQTT borker {}, retry count {}", brokerDTO.getHost(), context.getRetryCount(), e);
                    throw new RuntimeException(e);
                }
                return null;
            });

            try (ConsumerBenchmarkContext benchmarkContext = new ConsumerBenchmarkContext(command, benchmarkFlux, cancelRunningTasksRequestFlux)) {
                mqttClient.subscribe(command.getDestination(), command.getMqttQos(), (topic, message) -> benchmarkContext.onNewMessage());
            }

            if (mqttClient.isConnected()) {
                mqttClient.disconnect();
            }

        }

    }

}
