package com.monitoring.query.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record QueryResponse(
        String serviceName,
        Instant from,
        Instant to,
        PageResponse<MetricResponse> metrics,
        PageResponse<LogResponse> logs
) {
}
