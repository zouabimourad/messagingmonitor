package com.typesafe.messagingmonitor;

import org.apache.commons.lang3.StringUtils;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import reactor.core.publisher.Flux;

import javax.jms.DeliveryMode;
import javax.jms.*;
import java.util.List;

@Configuration
public class MonitorConfiguration {

    @Bean
    public ConnectionFactory connectionFactory(AppProperties appProperties) {
        String url = String.format("failover:amqp://%s:%s", appProperties.getBrokerHost(), appProperties.getBrokerPort());
        JmsConnectionFactory jmsConnectionFactory;
        if (StringUtils.isNotBlank(appProperties.getBrokerUsername())) {
            jmsConnectionFactory = new JmsConnectionFactory(appProperties.getBrokerUsername(), appProperties.getBrokerPassword(), url);
        } else {
            jmsConnectionFactory = new JmsConnectionFactory(url);
        }
        jmsConnectionFactory.setClientID("MM-MONITOR");
        return new SingleConnectionFactory(jmsConnectionFactory);
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setMessageConverter(messageConverter);
        jmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);
        jmsTemplate.setPubSubDomain(true);
        return jmsTemplate;
    }

    @Bean
    public MessageConverter simpleMessageConverter() {
        return new SimpleMessageConverter();
    }

    @Bean
    public JMSContext jmsContext(ConnectionFactory connectionFactory) {
        return connectionFactory.createContext();
    }

    @Bean
    public Flux<AgentDTO> agentFlux(MessageConverter messageConverter, JMSContext jmsContext) {
        return getTopicFLux(jmsContext, DestinationConstants.TOPIC_agentSynchro, messageConverter);
    }

    @Bean
    public Flux<FlowReport> reportFlux(MessageConverter messageConverter, JMSContext jmsContext) {
        return getTopicFLux(jmsContext, DestinationConstants.TOPIC_report, messageConverter);
    }

    @Bean
    public Flux<List<RunningTaskSummary<? extends AbstractCommand>>> runningTasksFlux(MessageConverter messageConverter, JMSContext jmsContext) {
        return getTopicFLux(jmsContext, DestinationConstants.TOPIC_agent_runningTasks, messageConverter);
    }

    public <T> Flux<T> getTopicFLux(JMSContext jmsContext, String topicName, MessageConverter messageConverter) {
        JMSConsumer consumer = jmsContext.createConsumer(jmsContext.createTopic(topicName));
        return Flux.create(emitter -> consumer.setMessageListener(message -> {
            try {
                emitter.next((T) messageConverter.fromMessage(message));
            } catch (JMSException e) {
                emitter.error(e);
            }
        }));
    }

}
