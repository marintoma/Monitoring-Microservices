package com.monitoring.notification.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record NotificationResponse(
        Long id,
        AlertRuleResponse alertRule,
        Double triggerValue,
        Instant firedAt
) {
}
