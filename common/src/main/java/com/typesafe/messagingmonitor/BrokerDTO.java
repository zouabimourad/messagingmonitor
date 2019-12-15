package com.typesafe.messagingmonitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrokerDTO implements Serializable {

    private String publicId;

    private String host;

    private boolean secured;

    private String username;

    private String password;

    boolean supportsMqtt;
    Integer mqttPort;

    boolean supportsAmqp;
    Integer amqpPort;

    public boolean isEmbedded() {
        return Objects.equals(getPublicId(), CommonConstants.EMBEDDED_BROKER_PUBLIC_ID);
    }
}
