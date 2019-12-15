package com.typesafe.messagingmonitor.command;

import com.typesafe.messagingmonitor.*;
import com.typesafe.messagingmonitor.agent.AgentService;
import com.typesafe.messagingmonitor.broker.BrokerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Service;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommandService {

    private final AgentService agentService;

    private final BrokerService brokerService;

    private final JmsTemplate jmsTemplate;

    private final JMSContext jmsContext;

    private final MessageConverter messageConverter;

    public void requestRunningTasks() {
        jmsContext.createProducer().send(jmsContext.createTopic(DestinationConstants.TOPIC_agent_runningTasksRequest), new RunningTasksRequestDTO());
    }

    public List<RunningTaskSummary<ConsumerCommand>> executeConsumerCommand(ConsumerCommand command) {
        return executeCommand(command, PeerTypeEnum.consumer);
    }

    public List<RunningTaskSummary<ProducerCommand>> executeProducerCommand(ProducerCommand command) {
        return executeCommand(command, PeerTypeEnum.producer);
    }

    public <T extends AbstractCommand> List<RunningTaskSummary<T>> executeCommand(T command, PeerTypeEnum peerTypeEnum) {
        AgentDTO agent = agentService.findAgentByPublicId(command.getAgentPublicId());
        BrokerDTO broker;

        if (command.getBrokerPublicId().equals(CommonConstants.EMBEDDED_BROKER_PUBLIC_ID)) {
            broker = new BrokerDTO();
            broker.setPublicId(CommonConstants.EMBEDDED_BROKER_PUBLIC_ID);
        } else {
            broker = brokerService.findBrokerByPublicId(command.getBrokerPublicId());
        }

        String topicName = "mm/agent/" + agent.getPublicId() + "/" + peerTypeEnum.name() + "Command";
        Message response = jmsTemplate.sendAndReceive(topicName, session -> {
            Message message = messageConverter.toMessage(new AgentCommand<>(command, broker), session);
            message.setJMSCorrelationID(UUID.randomUUID().toString());
            return message;
        });

        List<RunningTaskSummary<T>> runningTaskSummary;
        try {
            runningTaskSummary = (List<RunningTaskSummary<T>>) messageConverter.fromMessage(response);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

        return runningTaskSummary;
    }

    public void cancelRunningTasks() {
        jmsContext.createProducer().send(jmsContext.createTopic(DestinationConstants.TOPIC_agent_cancelRunningTasksRequest), new CancelRunningTasksRequestDTO());
    }

    public ConsumerCommand getConsumerDefaultCommand() {
        ConsumerCommand command = new ConsumerCommand();
        fillDefaultCommand(command);
        command.setMessagesCount(10000);
        command.setConsumptionPeriod(5);
        return command;
    }

    public ProducerCommand getProducerDefaultCommand() {
        ProducerCommand command = new ProducerCommand();
        fillDefaultCommand(command);
        command.setMessagesCount(10000);
        command.setMessagesInterval(5);
        command.setJmsDeliveryMode(DeliveryMode.NON_PERSISTENT);
        command.setMqttMaxInFlight(10);
        return command;
    }

    private void fillDefaultCommand(AbstractCommand abstractCommand) {
        abstractCommand.setProtocol(Protocol.MQTT);
        abstractCommand.setBrokerPublicId("EMBEDDED");
        abstractCommand.setDestination("T/test");
        abstractCommand.setMqttQos(0);
        abstractCommand.setJmsDestinationType(DestinationType.TOPIC);
    }


}
