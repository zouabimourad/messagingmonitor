package com.typesafe.messagingmonitor;

public enum Protocol {

    MQTT(1883),
    AMQP(5672);

    Protocol(int defaultPort) {
        this.defaultPort = defaultPort;
    }

    int defaultPort;

    public int getDefaultPort() {
        return defaultPort;
    }

    public String getCode() {
        return name();
    }
}
