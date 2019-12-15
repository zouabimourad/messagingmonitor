package com.typesafe.messagingmonitor.consumer;

import com.typesafe.messagingmonitor.AgentCommand;
import com.typesafe.messagingmonitor.ConsumerCommand;
import com.typesafe.messagingmonitor.RunningTaskSummary;
import com.typesafe.messagingmonitor.common.CommandServiceDelegate;
import com.typesafe.messagingmonitor.consumer.executors.ConsumerCommandExecutorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ConsumerService {

    private final ConsumerCommandExecutorFactory factory;

    private final CommandServiceDelegate commandServiceDelegate;

    public List<RunningTaskSummary<ConsumerCommand>> executeCommand(AgentCommand<ConsumerCommand> consumerCommand) {
        return commandServiceDelegate.executeCommand(factory.getExecutor(consumerCommand.getCommand().getProtocol()), consumerCommand);
    }

}