package com.typesafe.messagingmonitor.agent;

import com.typesafe.messagingmonitor.AgentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AgentService {

    private final AgentRepository agentRepository;
    private final AgentMapper agentMapper;
    private final Flux<AgentDTO> agentFLux;

    @PostConstruct
    public void init() {
        agentFLux.subscribe(this::saveAgent);
    }

    public List<AgentDTO> getAgents() {
        return agentRepository.findAll().stream().map(agentMapper::toDTO).collect(Collectors.toList());
    }

    public AgentDTO findAgentByPublicId(String publicId) {
        return agentMapper.toDTO(agentRepository.findByPublicId(publicId).orElseThrow(RuntimeException::new));
    }

    // TODO: replace synchronized by Database locking
    public void saveAgent(AgentDTO agentDTO) {
        Optional<Agent> agentOptional = agentRepository.findByPublicId(agentDTO.getPublicId());
        if (agentOptional.isPresent()) {
            Agent agent = agentOptional.get();
            if (agentDTO.getDetails() != null) {
                agent.setDetails(agentDTO.getDetails());
            }
            agentRepository.save(agent);
        } else {
            agentRepository.save(agentMapper.toPO(agentDTO));
        }
        log.info("Agent {} sync", agentDTO.getPublicId());
    }

}
