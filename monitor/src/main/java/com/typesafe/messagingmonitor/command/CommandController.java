package com.typesafe.messagingmonitor.command;

import com.typesafe.messagingmonitor.ConsumerCommand;
import com.typesafe.messagingmonitor.ProducerCommand;
import com.typesafe.messagingmonitor.Protocol;
import com.typesafe.messagingmonitor.RunningTaskSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommandController {

    private final CommandService commandService;

    @GetMapping(value = "/api/protocols")
    public Flux<ProtocolDTO> getProtocols() {
        return Flux.fromArray(Protocol.values()).map(p -> {
            ProtocolDTO protocolDTO = new ProtocolDTO();
            protocolDTO.setCode(p.getCode());
            protocolDTO.setDefaultPort(p.getDefaultPort());
            return protocolDTO;
        });
    }

    @GetMapping(value = "/api/requestRunningTasks")
    public void requestRunningTasks() {
        commandService.requestRunningTasks();
    }

    @PostMapping(value = "/api/cancelRunningTasks")
    public void cancelRunningTasks() {
        commandService.cancelRunningTasks();
    }

    @PostMapping(value = "/api/consumer")
    public List<RunningTaskSummary<ConsumerCommand>> executeConsumerCommand(@RequestBody ConsumerCommand command) {
        return commandService.executeConsumerCommand(command);
    }

    @PostMapping(value = "/api/producer")
    public List<RunningTaskSummary<ProducerCommand>> executeProducerCommand(@RequestBody ProducerCommand command) {
        return commandService.executeProducerCommand(command);
    }

    @GetMapping(value = "/api/consumer/defaultCommand")
    public ConsumerCommand getConsumerDefaultCommand() {
        return commandService.getConsumerDefaultCommand();
    }

    @GetMapping(value = "/api/producer/defaultCommand")
    public ProducerCommand getProducerDefaultCommand() {
        return commandService.getProducerDefaultCommand();
    }
}
