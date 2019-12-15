package com.typesafe.messagingmonitor;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentCommand<COMMAND extends AbstractCommand> implements Serializable {

    COMMAND command;

    BrokerDTO broker;

}



