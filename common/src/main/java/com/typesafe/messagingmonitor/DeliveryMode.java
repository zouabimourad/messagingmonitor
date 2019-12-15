package com.typesafe.messagingmonitor;

public enum DeliveryMode {

    NON_PERSISTENT(javax.jms.DeliveryMode.NON_PERSISTENT),

    PERSISTENT(javax.jms.DeliveryMode.PERSISTENT);

    DeliveryMode(int code) {
        this.code = code;
    }

    int code;

    public int getCode() {
        return code;
    }
}


