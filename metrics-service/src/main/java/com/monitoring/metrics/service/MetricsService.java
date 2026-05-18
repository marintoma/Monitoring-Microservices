package com.monitoring.metrics.service;

import com.monitoring.metrics.repo.MetricsRepository;
import com.monitoring.metrics.common.BatchIngestResponse;
import com.monitoring.metrics.exception.BatchSizeExceededException;
import com.monitoring.metrics.dto.MetricRequest;
import com.monitoring.metrics.dto.MetricResponse;
import com.monitoring.metrics.entity.Metric;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final MetricsRepository repo;

    @Value("${app.batch.max-size}")
    private int maxBatchSize;

    @Transactional
    public MetricResponse ingest(MetricRequest metricRequest) {
        Metric metric = Metric.builder()
                .name(metricRequest.name())
                .value(metricRequest.value())
                .serviceName(metricRequest.serviceName())
                .timestamp(metricRequest.timestamp())
                .build();

        Metric saved = repo.save(metric);

        return toResponse(saved);
    }

    @Transactional
    public BatchIngestResponse ingestBatch(List<MetricRequest> requests) {
        if (requests.size() > maxBatchSize) {
            throw new BatchSizeExceededException(requests.size(), maxBatchSize);
        }

        int ingested = 0;
        int failed = 0;

        for (MetricRequest request : requests) {
            try {
                Metric metric = Metric.builder()
                        .name(request.name())
                        .value(request.value())
                        .serviceName(request.serviceName())
                        .timestamp(request.timestamp())
                        .build();
                repo.save(metric);
                ingested++;
            } catch (Exception e) {
                failed++;
            }
        }

        return BatchIngestResponse.builder()
                .ingested(ingested)
                .failed(failed)
                .timestamp(Instant.now())
                .build();
    }

    public Page<MetricResponse> findByServiceName(String serviceName, Pageable pageable) {
        return repo.findByServiceName(serviceName, pageable)
                .map(this::toResponse);
    }

    public Page<MetricResponse> findByName(String name, Pageable pageable) {
        return repo.findByName(name, pageable)
                .map(this::toResponse);
    }

    public Page<MetricResponse> findByTimestampBetween(Instant from, Instant to, Pageable pageable) {
        return repo.findByTimestampBetween(from, to, pageable)
                .map(this::toResponse);
    }

    public Page<MetricResponse> findByServiceNameAndTimeRange(String serviceName, Instant from, Instant to, Pageable pageable) {
        return repo.findByServiceNameAndTimestampBetween(serviceName, from, to, pageable)
                .map(this::toResponse);
    }

    private MetricResponse toResponse(Metric metric) {
        return MetricResponse.builder()
                .id(metric.getId())
                .name(metric.getName())
                .value(metric.getValue())
                .serviceName(metric.getServiceName())
                .timestamp(metric.getTimestamp())
                .build();
    }
}
