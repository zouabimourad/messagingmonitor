package com.typesafe.messagingmonitor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import reactor.core.publisher.Flux;

import javax.jms.DeliveryMode;
import javax.jms.*;

@Slf4j
@Configuration
public class AgentConfiguration {

    @Bean
    public RetryTemplate retryTemplate() {
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(1000L);

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(3));
        return retryTemplate;
    }

    @Bean
    public ConnectionFactory connectionFactory(AppProperties appProperties) {
        String url = String.format("failover:amqp://%s:%s", appProperties.getBrokerHost(), appProperties.getBrokerPort());
        JmsConnectionFactory jmsConnectionFactory;
        if (StringUtils.isNotBlank(appProperties.getBrokerUsername())) {
            jmsConnectionFactory = new JmsConnectionFactory(appProperties.getBrokerUsername(), appProperties.getBrokerPassword(), url);
        } else {
            jmsConnectionFactory = new JmsConnectionFactory(url);
        }
        jmsConnectionFactory.setClientID("MM-AGENT-" + appProperties.getIdentifier());
        return new SingleConnectionFactory(jmsConnectionFactory);
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setPubSubDomain(true);
        return factory;
    }

    @Bean
    public JMSContext jmsContext(ConnectionFactory connectionFactory) {
        return connectionFactory.createContext();
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setMessageConverter(messageConverter);
        jmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        jmsTemplate.setPubSubDomain(true);
        return jmsTemplate;
    }

    @Bean
    public MessageConverter simpleMessageConverter() {
        return new SimpleMessageConverter();
    }

    @Bean
    public Flux<AgentSynchroRequestDTO> agentSynchroRequestFlux(MessageConverter messageConverter, JMSContext jmsContext) {
        return getTopicFLux(jmsContext, DestinationConstants.TOPIC_agentSynchroRequest, messageConverter);
    }

    @Bean
    public Flux<RunningTasksRequestDTO> runningTasksRequestFlux(MessageConverter messageConverter, JMSContext jmsContext) {
        return getTopicFLux(jmsContext, DestinationConstants.TOPIC_agent_runningTasksRequest, messageConverter);
    }

    @Bean
    public Flux<CancelRunningTasksRequestDTO> cancelRunningTasksRequestFlux(MessageConverter messageConverter, JMSContext jmsContext) {
        Flux<CancelRunningTasksRequestDTO> flux = getTopicFLux(jmsContext, DestinationConstants.TOPIC_agent_cancelRunningTasksRequest, messageConverter);
        return flux.share();
    }

//    @Bean("consumerCommandFlux")
//    public Flux<RunningTaskSummary<ConsumerCommand>> consumerCommandFlux(@Value("#{'mm/agent/' + appProperties.identifier + '/consumerCommand'}") String topicName, MessageConverter messageConverter, JMSContext jmsContext) {
//        return getTopicFLux(jmsContext, topicName, messageConverter);
//    }
//
//    @Bean("producerCommandFlux")
//    public Flux<RunningTaskSummary<ProducerCommand>> producerCommandFlux(@Value("#{'mm/agent/' + appProperties.identifier + '/producerCommand'}") String topicName,MessageConverter messageConverter, JMSContext jmsContext) {
//        return getTopicFLux(jmsContext, topicName, messageConverter);
//    }

    public <T> Flux<T> getTopicFLux(JMSContext jmsContext, String topicName, MessageConverter messageConverter) {
        JMSConsumer consumer = jmsContext.createConsumer(jmsContext.createTopic(topicName));
        return Flux.create(emitter -> consumer.setMessageListener(message -> {
            try {
                emitter.next((T) messageConverter.fromMessage(message));
            } catch (JMSException e) {
                log.error(e.getMessage(), e);
            }
        }));
    }

}
