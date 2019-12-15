package com.typesafe.messagingmonitor.common;

import com.typesafe.messagingmonitor.ExecutionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BenchmarkEvent {

    long eventCount;
    long elapsedTime;
    long rate;
    long averageRate;
    ExecutionStatus status;

}
