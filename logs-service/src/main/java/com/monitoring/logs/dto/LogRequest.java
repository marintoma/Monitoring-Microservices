package com.monitoring.logs.dto;

import com.monitoring.logs.enums.LogLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;

@Builder
public record LogRequest(
        @NotNull LogLevel level,
        @NotBlank String message,
        @NotBlank String serviceName,
        @NotNull Instant timestamp
) {
}
