package com.typesafe.messagingmonitor.broker;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class Broker {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String publicId;

    private String host;

    private boolean secured;

    private String username;

    private String password;

    boolean supportsMqtt;

    Integer mqttPort;

    boolean supportsAmqp;

    Integer amqpPort;

    @PrePersist
    public void preInsert() {
        if(publicId == null) {
            publicId = UUID.randomUUID().toString();
        }
    }

}
