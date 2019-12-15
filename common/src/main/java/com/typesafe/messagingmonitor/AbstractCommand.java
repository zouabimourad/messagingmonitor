package com.typesafe.messagingmonitor;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Data
public abstract class AbstractCommand implements Serializable {

    PeerTypeEnum type;

    String agentPublicId;

    Protocol protocol;

    String brokerPublicId;

    String clientId;

    String destination;

    int samplingInterval = 1000;

    int mqttQos = 0;

    int mqttMaxInFlight;

    boolean async = false;

    DestinationType jmsDestinationType = DestinationType.TOPIC;

    int commandsCount = 1;

    public String buildEffectiveClientId(String requestId, int commandIndex) {
        return ofNullable(defaultIfBlank(getClientId(), null))
                .map(value -> value + (commandsCount > 1 ? String.valueOf(commandIndex) : StringUtils.EMPTY))
                .orElseGet(() -> String.join("-", "MM", getProtocol().name(), getType().name(), requestId));
    }

}
