package com.typesafe.messagingmonitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;

@Slf4j
@Configuration
public class WebSocketConfiguration {

    @Bean
    WebSocketHandlerAdapter socketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    HandlerMapping webSocketURLMapping(Flux<FlowReport> reportPublisher,
                                       Flux<List<RunningTaskSummary<? extends AbstractCommand>>> runningTasks,
                                       ObjectMapper objectMapper) {

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();

        Map<String, WebSocketHandler> urlMap = new HashMap<>();
        urlMap.put("/api/report", buildWebSocketHandler(reportPublisher, objectMapper));
        urlMap.put("/api/runningTasks", buildWebSocketHandler(runningTasks, objectMapper));

        handlerMapping.setUrlMap(urlMap);
        handlerMapping.setCorsConfigurations(singletonMap("*", new CorsConfiguration().applyPermitDefaultValues()));
        handlerMapping.setOrder(10);
        return handlerMapping;
    }

    private WebSocketHandler buildWebSocketHandler(Flux<?> reportPublisher, ObjectMapper objectMapper) {
        return session ->
                session.send(reportPublisher
                        .map(report -> {
                            try {
                                return objectMapper.writeValueAsString(report);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .map(session::textMessage)
                );
    }

}
