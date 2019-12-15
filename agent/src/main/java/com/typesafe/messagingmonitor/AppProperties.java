package com.typesafe.messagingmonitor;


import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Data
@ConfigurationProperties(prefix = "agent")
@Component
@Validated
public class AppProperties {

    String identifier;
    String brokerHost;
    String brokerPort;
    String brokerUsername;
    String brokerPassword;

    @PostConstruct
    public void init() {
        if (StringUtils.isBlank(identifier)) {
            identifier = UUID.randomUUID().toString();
        }
    }

}
