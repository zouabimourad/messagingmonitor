package com.typesafe.messagingmonitor.agent;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String publicId;

    private String details;

    @PrePersist
    public void preInsert() {
        if (publicId == null) {
            publicId = UUID.randomUUID().toString();
        }
    }
}
