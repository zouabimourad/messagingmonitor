package com.typesafe.messagingmonitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AgentSynchroRequestDTO implements Serializable  {

    List<BrokerDTO> brokers;

}
