package com.monitoring.logs.exception;

import lombok.Builder;

import java.time.Instant;

@Builder
public record APIError(
        int status,
        String message,
        Instant timestamp
) {
}
