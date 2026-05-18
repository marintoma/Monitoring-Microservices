package com.monitoring.logs.dto;

import com.monitoring.logs.enums.LogLevel;
import lombok.Builder;

import java.time.Instant;

@Builder
public record LogResponse(
    Long id,
    LogLevel level,
    String message,
    String serviceName,
    Instant timestamp
) {
}
