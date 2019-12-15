package com.typesafe.messagingmonitor.common.mqtt;

import org.eclipse.paho.client.mqttv3.*;

public class MqttClientAdapter implements AutoCloseable {

    MqttAsyncClient mqttAsyncClient;

    MqttClient mqttClient;

    public MqttClientAdapter(MqttAsyncClient mqttAsyncClient) {
        this.mqttAsyncClient = mqttAsyncClient;
    }

    public MqttClientAdapter(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    @Override
    public void close() throws MqttException {
        if (mqttAsyncClient != null) {
            mqttAsyncClient.close();
        } else {
            mqttClient.close();
        }
    }

    public void connect(MqttConnectOptions options) throws MqttException {
        if (mqttAsyncClient != null) {
            IMqttToken token = mqttAsyncClient.connect(options);
            token.waitForCompletion();
        } else {
            mqttClient.connect(options);
        }
    }

    public void publish(String destination, MqttMessage message) throws MqttException {
        if (mqttAsyncClient != null) {
            mqttAsyncClient.publish(destination, message);
        } else {
            mqttClient.publish(destination, message);
        }
    }

    public void publishAsync(String destination, MqttMessage message, IMqttActionListener listener) throws MqttException {
        if (mqttAsyncClient != null) {
            mqttAsyncClient.publish(destination, message, null, listener);
        }
    }

    public boolean isConnected() {
        if (mqttAsyncClient != null) {
            return mqttAsyncClient.isConnected();
        } else {
            return mqttClient.isConnected();
        }
    }

    public void disconnect() throws MqttException {
        if (mqttAsyncClient != null) {
            mqttAsyncClient.disconnect();
        } else {
            mqttClient.disconnect();
        }
    }

    public void subscribe(String destination, int qos, IMqttMessageListener listener) throws MqttException {
        if (mqttAsyncClient != null) {
            mqttAsyncClient.subscribe(destination, qos, listener);
        } else {
            mqttClient.subscribe(destination, qos, listener);
        }
    }

}
