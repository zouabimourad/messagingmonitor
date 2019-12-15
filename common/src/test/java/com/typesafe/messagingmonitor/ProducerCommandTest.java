package com.typesafe.messagingmonitor;

import org.junit.Assert;
import org.junit.Test;

public class ProducerCommandTest {

    @Test
    public void test() {

        ProducerCommand producerCommand = new ProducerCommand();
        producerCommand.setCustomMessage(false);
        producerCommand.setMessageSize(2);

        Assert.assertEquals(1024 * 2, producerCommand.buildEffectiveMessage().getBytes().length);

    }

}