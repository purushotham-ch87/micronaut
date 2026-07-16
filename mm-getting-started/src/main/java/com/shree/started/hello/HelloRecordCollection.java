package com.shree.started.hello;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public record HelloRecordCollection(
           List<String> msgList
) {
    public HelloRecordCollection(List<String> msgList) {
        this.msgList = List.copyOf(msgList);
    }
}
