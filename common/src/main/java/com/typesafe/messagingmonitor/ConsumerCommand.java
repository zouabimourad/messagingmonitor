package com.typesafe.messagingmonitor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class ConsumerCommand extends AbstractCommand {

    boolean jmsDurableConsumer;

    String jmsConsumerName;

    boolean jmsSharedConsumer;

    Integer consumptionPeriod;

    Integer messagesCount;

    public ConsumerCommand() {
        type = PeerTypeEnum.consumer;
    }
}
