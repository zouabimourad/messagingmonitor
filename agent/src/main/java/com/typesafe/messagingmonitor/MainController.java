package com.typesafe.messagingmonitor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;

@RestController
public class MainController {

    @RequestMapping("/")
    public Map root() {
        return of("type", "agent");
    }

}
