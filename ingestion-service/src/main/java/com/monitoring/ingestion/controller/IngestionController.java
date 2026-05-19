package com.monitoring.ingestion.controller;

import com.monitoring.ingestion.dto.*;
import com.monitoring.ingestion.exception.BatchSizeExceededException;
import com.monitoring.ingestion.forwarder.LogsForwarder;
import com.monitoring.ingestion.forwarder.MetricsForwarder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingest")
@RequiredArgsConstructor
public class IngestionController {

    private final MetricsForwarder metricsForwarder;
    private final LogsForwarder logsForwarder;

    @Value("${app.batch.max-size}")
    private int maxBatchSize;

    @PostMapping("/metrics")
    @ResponseStatus(HttpStatus.CREATED)
    public MetricResponse ingestMetric(@Valid @RequestBody MetricRequest request) {
        return metricsForwarder.forward(request);
    }

    @PostMapping("/metrics/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public BatchIngestResponse ingestMetricBatch(@Valid @RequestBody List<MetricRequest> requests) {
        if (requests.size() > maxBatchSize) {
            throw new BatchSizeExceededException(requests.size(), maxBatchSize);
        }
        return metricsForwarder.forwardBatch(requests);
    }

    @PostMapping("/logs")
    @ResponseStatus(HttpStatus.CREATED)
    public LogResponse ingestLog(@Valid @RequestBody LogRequest request) {
        return logsForwarder.forward(request);
    }

    @PostMapping("/logs/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public BatchIngestResponse ingestLogBatch(@Valid @RequestBody List<LogRequest> requests) {
        if (requests.size() > maxBatchSize) {
            throw new BatchSizeExceededException(requests.size(), maxBatchSize);
        }
        return logsForwarder.forwardBatch(requests);
    }
}
