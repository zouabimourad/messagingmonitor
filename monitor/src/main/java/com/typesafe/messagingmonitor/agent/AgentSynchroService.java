package com.typesafe.messagingmonitor.agent;

import com.typesafe.messagingmonitor.AgentSynchroRequestDTO;
import com.typesafe.messagingmonitor.BrokerDTO;
import com.typesafe.messagingmonitor.DestinationConstants;
import com.typesafe.messagingmonitor.broker.BrokerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AgentSynchroService {

    private final BrokerService brokerService;
    private final JmsTemplate jmsTemplate;

    @Scheduled(initialDelay = 1000, fixedRate = 15000)
    public void requestAgentSynchro() {
        List<BrokerDTO> brokers = brokerService.getBrokers(false);
        jmsTemplate.convertAndSend(DestinationConstants.TOPIC_agentSynchroRequest, new AgentSynchroRequestDTO(brokers));
    }

}
