package com.monitoring.notification.dto;

import com.monitoring.notification.enums.ThresholdDirection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AlertRuleRequest(
        @NotBlank String metricName,
        @NotBlank String serviceName,
        @NotNull Double threshold,
        @NotNull ThresholdDirection direction,
        @NotNull Long cooldownSeconds
) {
}
