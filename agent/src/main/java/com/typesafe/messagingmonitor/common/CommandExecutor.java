package com.typesafe.messagingmonitor.common;

import com.typesafe.messagingmonitor.AbstractCommand;
import com.typesafe.messagingmonitor.AgentCommand;

public interface CommandExecutor<COMMAND extends AbstractCommand> {

    void execute(String requestId, String clientId, AgentCommand<COMMAND> command, BenchmarkFlux benchmarkFlux) throws Exception;

}
