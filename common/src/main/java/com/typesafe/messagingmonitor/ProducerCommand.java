package com.typesafe.messagingmonitor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class ProducerCommand extends AbstractCommand {

    boolean customMessage;

    String message;

    Integer messageSize;

    boolean templated = false;

    DeliveryMode jmsDeliveryMode = DeliveryMode.NON_PERSISTENT;

    Long jmsTimeToLive;

    int messagesCount = 1000;

    int messagesInterval = 1;

    int mqttMaxInFlight = 10;

    boolean async = false;

    public ProducerCommand() {
        type = PeerTypeEnum.producer;
    }

    public String buildEffectiveMessage() {
        if (customMessage) {
            return message;
        } else {
            return RandomStringUtils.random(messageSize * 1024, true, true);
        }
    }

}



