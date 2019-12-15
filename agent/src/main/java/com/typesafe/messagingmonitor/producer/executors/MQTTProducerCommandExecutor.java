package com.typesafe.messagingmonitor.producer.executors;

import com.typesafe.messagingmonitor.AgentCommand;
import com.typesafe.messagingmonitor.BrokerDTO;
import com.typesafe.messagingmonitor.CancelRunningTasksRequestDTO;
import com.typesafe.messagingmonitor.ProducerCommand;
import com.typesafe.messagingmonitor.common.BenchmarkFlux;
import com.typesafe.messagingmonitor.common.CommandExecutor;
import com.typesafe.messagingmonitor.common.mqtt.MqttClientAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;

import java.util.concurrent.Semaphore;

import static com.typesafe.messagingmonitor.common.mqtt.MQTTHelper.buildMqttClientWrapper;
import static com.typesafe.messagingmonitor.common.mqtt.MQTTHelper.buildOptions;

@Slf4j
@Service
@RequiredArgsConstructor
public class MQTTProducerCommandExecutor implements CommandExecutor<ProducerCommand> {

    private final RetryTemplate retryTemplate;

    private final Flux<CancelRunningTasksRequestDTO> cancelRunningTasksRequestFlux;

    @Override
    public void execute(String requestId, String clientId, AgentCommand<ProducerCommand> agentCommand, BenchmarkFlux benchmarkFlux) throws MqttException {

        ProducerCommand command = agentCommand.getCommand();
        BrokerDTO brokerDTO = agentCommand.getBroker();

        try (MqttClientAdapter mqttClientAdapter = buildMqttClientWrapper(command.isAsync(), brokerDTO.getHost(), brokerDTO.isSecured(), brokerDTO.getMqttPort(), clientId)) {

            retryTemplate.execute((RetryCallback<Void, RuntimeException>) context -> {
                try {
                    mqttClientAdapter.connect(buildOptions(brokerDTO.getUsername(), brokerDTO.getPassword(), command.getMqttMaxInFlight()));
                } catch (Exception e) {
                    log.error("failed to connect to MQTT borker {}, retry count {}", brokerDTO.getHost(), context.getRetryCount(), e);
                    throw new RuntimeException(e);
                }
                return null;
            });

            ProducerBenchmarkContext producerBenchmarkContext = new ProducerBenchmarkContext(command, benchmarkFlux, cancelRunningTasksRequestFlux);

            // max in flight is not controller by PAHO .. we need a semaphore to throttle
            Semaphore semaphore = new Semaphore(command.getMqttMaxInFlight());

            final byte[] messageAsByteArray = command.buildEffectiveMessage().getBytes();

            if (command.isAsync()) {

                DirectProcessor<IMqttToken> ackFlux = DirectProcessor.create();
                IMqttActionListener listener = new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        ackFlux.onNext(asyncActionToken);
                        semaphore.release();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        semaphore.release();
                        ackFlux.onNext(asyncActionToken);
                        log.error(exception.getMessage(), exception);
                    }
                };

                producerBenchmarkContext.startAsync(() -> {
                    try {
                        semaphore.acquire();
                        MqttMessage message = new MqttMessage(messageAsByteArray);
                        message.setQos(command.getMqttQos());
                        mqttClientAdapter.publishAsync(command.getDestination(), message, listener);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        throw new RuntimeException(e);
                    }

                }, ackFlux);

            } else {

                producerBenchmarkContext.start(() -> {
                    MqttMessage message = new MqttMessage(messageAsByteArray);
                    message.setQos(command.getMqttQos());

                    try {
                        mqttClientAdapter.publish(command.getDestination(), message);
                    } catch (MqttException e) {
                        log.error(e.getMessage());
                        throw new RuntimeException(e);
                    }

                });

            }

            if (mqttClientAdapter.isConnected()) {
                mqttClientAdapter.disconnect();
            }
        }

    }

}
