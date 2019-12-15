package com.typesafe.messagingmonitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class SynchroService {

    private final AppProperties appProperties;
    private final JmsTemplate jmsTemplate;
    private final Flux<AgentSynchroRequestDTO> agentSynchroRequestFlux;

    @PostConstruct
    public void init() {
        agentSynchroRequestFlux.subscribe(this::onAgentSynchroRequest);
    }

    public void onAgentSynchroRequest(AgentSynchroRequestDTO agentSynchroRequestDTO) {

        // send basic information
        AgentDTO agentDTO = new AgentDTO(appProperties.getIdentifier(), null);
        jmsTemplate.convertAndSend(DestinationConstants.TOPIC_agentSynchro, agentDTO);

        // send full informations
        String details = agentSynchroRequestDTO.getBrokers().stream().map(brokerDTO -> {

            Duration ping = ping(brokerDTO.getHost());

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Broker : ").append(brokerDTO.getHost()).append(", Reachable : ");
            if (ping == null) {
                stringBuilder.append("No");
            } else {
                stringBuilder.append("Yes, Latency : ").append(ping.getNano() / 1000).append(" microseconds");
            }

            return stringBuilder.toString();

        }).collect(Collectors.joining("\n"));

        agentDTO = new AgentDTO(appProperties.getIdentifier(), details);
        jmsTemplate.convertAndSend(DestinationConstants.TOPIC_agentSynchro, agentDTO);

    }

    public Duration ping(String host) {
        Instant startTime = Instant.now();
        try {
            InetAddress address = InetAddress.getByName(host);
            if (address.isReachable(1000)) {
                return Duration.between(startTime, Instant.now());
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
