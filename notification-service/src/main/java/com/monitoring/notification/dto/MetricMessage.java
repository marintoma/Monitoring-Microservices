package com.monitoring.notification.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record MetricMessage(
        Long id,
        String name,
        Double value,
        String serviceName,
        Instant timestamp
) {
}
