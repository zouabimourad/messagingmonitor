package com.typesafe.messagingmonitor.common.jmsoveramqp;

import com.typesafe.messagingmonitor.DestinationType;
import org.apache.commons.lang3.StringUtils;
import org.apache.qpid.jms.JmsConnectionFactory;

import javax.jms.Destination;
import javax.jms.JMSContext;

public class JMSOverAMQPHelper {

    public static JMSContext buildJMSContext(String host, boolean secured, int port, String username, String password, String clientId) {
        String url = String.format("failover:amqp://%s:%s?ssl=%s&failover.maxReconnectAttempts=3", host, port, Boolean.valueOf(secured).toString());
        JmsConnectionFactory connectionFactory;
        if (StringUtils.isNotBlank(username)) {
            connectionFactory = new JmsConnectionFactory(username, password, url);
        } else {
            connectionFactory = new JmsConnectionFactory(url);
        }
        connectionFactory.setClientID(clientId);
        return connectionFactory.createContext();
    }

    public static Destination buildDestination(JMSContext jmsContext, String name, DestinationType destinationType) {
        if (destinationType == DestinationType.TOPIC) {
            return jmsContext.createTopic(name);
        } else {
            return jmsContext.createQueue(name);
        }
    }

}
