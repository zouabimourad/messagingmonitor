package com.typesafe.messagingmonitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentDTO implements Serializable {

    private String publicId;

    private String details;

}
