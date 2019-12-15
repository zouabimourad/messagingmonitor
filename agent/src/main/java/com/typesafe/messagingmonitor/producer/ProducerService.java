package com.typesafe.messagingmonitor.producer;

import com.typesafe.messagingmonitor.AgentCommand;
import com.typesafe.messagingmonitor.ProducerCommand;
import com.typesafe.messagingmonitor.RunningTaskSummary;
import com.typesafe.messagingmonitor.common.CommandServiceDelegate;
import com.typesafe.messagingmonitor.producer.executors.ProducerCommandExecutorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProducerService {

    private final ProducerCommandExecutorFactory factory;

    private final CommandServiceDelegate commandServiceDelegate;

    public List<RunningTaskSummary<ProducerCommand>> executeCommand(AgentCommand<ProducerCommand> producerCommand) {
        return commandServiceDelegate.executeCommand(factory.getExecutor(producerCommand.getCommand().getProtocol()), producerCommand);
    }

}
