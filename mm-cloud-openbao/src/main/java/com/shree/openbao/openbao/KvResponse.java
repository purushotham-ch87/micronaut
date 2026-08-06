package com.shree.openbao.openbao;

import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;

@Serdeable
public record KvResponse(Data data) {
    @Serdeable
    public record Data(Map<String, String> data) {}
}