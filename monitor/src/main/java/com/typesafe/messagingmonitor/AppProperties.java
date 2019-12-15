package com.typesafe.messagingmonitor;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties(prefix = "monitor")
@Component
@Validated
public class AppProperties {

    String brokerHost;
    String brokerPort;
    String brokerUsername;
    String brokerPassword;

    String brokerDefaultHost;
    String brokerDefaultUsername;
    String brokerDefaultPassword;

}
