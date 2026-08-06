package com.shree.started.hello;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public record HelloRecordBean(
            String msgType,
           List<String> msgList
) {
    public HelloRecordBean(String msgType, List<String> msgList) {
        this.msgType = msgType;
        this.msgList = List.copyOf(msgList);
    }
}
