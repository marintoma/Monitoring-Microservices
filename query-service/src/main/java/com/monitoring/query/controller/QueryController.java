package com.monitoring.query.controller;

import com.monitoring.query.dto.LogResponse;
import com.monitoring.query.dto.MetricResponse;
import com.monitoring.query.dto.PageResponse;
import com.monitoring.query.dto.QueryResponse;
import com.monitoring.query.forwarder.LogsForwarder;
import com.monitoring.query.forwarder.MetricsForwarder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/query")
@RequiredArgsConstructor
public class QueryController {

    private final MetricsForwarder metricsForwarder;
    private final LogsForwarder logsForwarder;

    @Value("${app.query.max-page-size}")
    private int maxPageSize;

    @GetMapping
    public QueryResponse query(
            @RequestParam String serviceName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0") int metricsPage,
            @RequestParam(defaultValue = "50") Integer metricsSize,
            @RequestParam(defaultValue = "0") int logsPage,
            @RequestParam(defaultValue = "50") Integer logsSize) {

        int resolvedMetricsSize = Math.min(metricsSize, maxPageSize);
        int resolvedLogsSize = Math.min(logsSize, maxPageSize);

        PageResponse<MetricResponse> metrics = metricsForwarder
                .findByServiceNameAndTimeRange(serviceName, from, to, metricsPage, resolvedMetricsSize);

        PageResponse<LogResponse> logs = logsForwarder
                .findByServiceNameAndTimeRange(serviceName, from, to, logsPage, resolvedLogsSize);

        return QueryResponse.builder()
                .serviceName(serviceName)
                .from(from)
                .to(to)
                .metrics(metrics)
                .logs(logs)
                .build();
    }
}
