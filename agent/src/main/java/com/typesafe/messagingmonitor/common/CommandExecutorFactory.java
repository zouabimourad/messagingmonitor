package com.typesafe.messagingmonitor.common;

import com.typesafe.messagingmonitor.AbstractCommand;
import com.typesafe.messagingmonitor.Protocol;

public interface CommandExecutorFactory<COMMAND extends AbstractCommand, COMMAND_EXECUTOR extends CommandExecutor<COMMAND>> {

    COMMAND_EXECUTOR getExecutor(Protocol protocol);

}
