package com.typesafe.messagingmonitor.broker;

import com.typesafe.messagingmonitor.BrokerDTO;
import org.springframework.stereotype.Component;

@Component
public class BrokerMapper {

    public Broker toPO(BrokerDTO brokerDTO) {
        Broker broker = new Broker();

        broker.setHost(brokerDTO.getHost());
        broker.setSecured(brokerDTO.isSecured());

        broker.setUsername(brokerDTO.getUsername());
        broker.setPassword(brokerDTO.getPassword());

        broker.setSupportsMqtt(brokerDTO.isSupportsMqtt());
        if (brokerDTO.isSupportsMqtt()) {
            broker.setMqttPort(brokerDTO.getMqttPort());
        }

        broker.setSupportsAmqp(brokerDTO.isSupportsAmqp());
        if (brokerDTO.isSupportsAmqp()) {
            broker.setAmqpPort(brokerDTO.getAmqpPort());
        }
        return broker;
    }

    public BrokerDTO toDTO(Broker broker) {
        return new BrokerDTO(
                broker.getPublicId(),
                broker.getHost(),
                broker.isSecured(),
                broker.getUsername(),
                broker.getPassword(),
                broker.isSupportsMqtt(),
                broker.getMqttPort(),
                broker.isSupportsAmqp(),
                broker.getAmqpPort()
        );
    }
}
