package com.monitoring.ingestion.forwarder;

import com.monitoring.ingestion.dto.BatchIngestResponse;
import com.monitoring.ingestion.dto.MetricRequest;
import com.monitoring.ingestion.dto.MetricResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MetricsForwarder {

    private final RestClient restClient;

    @Value("${services.metrics-url}")
    private String metricsUrl;

    public MetricResponse forward(MetricRequest request) {
        return restClient.post()
                .uri(metricsUrl + "/api/metrics")
                .body(request)
                .retrieve()
                .body(MetricResponse.class);
    }

    public BatchIngestResponse forwardBatch(List<MetricRequest> requests) {
        return restClient.post()
                .uri(metricsUrl + "/api/metrics/batch")
                .body(requests)
                .retrieve()
                .body(BatchIngestResponse.class);
    }
}
