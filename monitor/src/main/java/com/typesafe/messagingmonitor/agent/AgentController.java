package com.typesafe.messagingmonitor.agent;

import com.typesafe.messagingmonitor.AgentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/agents")
@RequiredArgsConstructor
@RestController
public class AgentController {

    private final AgentService agentService;

    @GetMapping
    public List<AgentDTO> getAgents() {
        return agentService.getAgents();
    }

}
