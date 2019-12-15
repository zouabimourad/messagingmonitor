package com.typesafe.messagingmonitor.common.mqtt;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import static org.eclipse.paho.client.mqttv3.MqttConnectOptions.MQTT_VERSION_3_1_1;

public class MQTTHelper {

    public static synchronized MqttClientAdapter buildMqttClientWrapper(boolean async, String host, boolean secured, int port, String clientId)
            throws MqttException {
        String url = String.format("%s://%s:%s", secured ? "ssl" : "tcp", host, port);
        if (async) {
            return new MqttClientAdapter(new MqttAsyncClient(url, clientId, new MemoryPersistence()));
        } else {
            return new MqttClientAdapter(new MqttClient(url, clientId, new MemoryPersistence()));
        }
    }

    public static MqttConnectOptions buildOptions(String username, String password) {
        return buildOptions(username, password, null);
    }

    public static MqttConnectOptions buildOptions(String username, String password, Integer maxInFlight) {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        if (StringUtils.isNotBlank(username)) {
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());
        }
        connOpts.setAutomaticReconnect(true);
        connOpts.setMaxReconnectDelay(5000);
        connOpts.setMqttVersion(MQTT_VERSION_3_1_1);
        if (maxInFlight != null) {
            connOpts.setMaxInflight(maxInFlight);
        }
        return connOpts;
    }

}
