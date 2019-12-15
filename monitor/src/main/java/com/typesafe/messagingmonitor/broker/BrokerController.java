package com.typesafe.messagingmonitor.broker;

import com.typesafe.messagingmonitor.BrokerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/brokers")
@RequiredArgsConstructor
@RestController
public class BrokerController {

    private final BrokerService brokerService;

    @GetMapping
    public List<BrokerDTO> getBrokers() {
        return brokerService.getBrokers(true);
    }

    @DeleteMapping("/{agentPublicId}")
    public void deleteBroker(@PathVariable String agentPublicId) {
        brokerService.deleteBroker(agentPublicId);
    }

    @PostMapping
    public void saveBroker(@RequestBody BrokerDTO brokerDTO) {
        brokerService.saveBroker(brokerDTO);
    }

}
