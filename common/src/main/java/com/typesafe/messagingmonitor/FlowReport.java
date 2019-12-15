package com.typesafe.messagingmonitor;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Data
public class FlowReport implements Serializable {

    protected String requestId;
    protected Long eventCount;
    protected Long elapsedTime;
    protected Long rate;
    protected Long averageRate;
    protected ExecutionStatus status;
}
