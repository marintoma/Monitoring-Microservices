package com.monitoring.notification.dto;

import com.monitoring.notification.enums.ThresholdDirection;
import lombok.Builder;

@Builder
public record AlertRuleResponse(
        Long id,
        String metricName,
        String serviceName,
        Double threshold,
        ThresholdDirection direction,
        Long cooldownSeconds
) {
}
