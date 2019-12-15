package com.typesafe.messagingmonitor.agent;

import com.typesafe.messagingmonitor.AgentDTO;
import org.springframework.stereotype.Component;

@Component
public class AgentMapper {

    public Agent toPO(AgentDTO agentDTO) {
        Agent agent = new Agent();
        agent.setPublicId(agentDTO.getPublicId());
        agent.setDetails(agentDTO.getDetails());
        return agent;
    }

    public AgentDTO toDTO(Agent agent) {
        return new AgentDTO(agent.getPublicId(), agent.getDetails());
    }
}
