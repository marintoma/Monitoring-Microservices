package com.monitoring.query.forwarder;

import com.monitoring.query.dto.MetricResponse;
import com.monitoring.query.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class MetricsForwarder {

    private final RestClient restClient;

    @Value("${services.metrics-url}")
    private String metricsUrl;

    public PageResponse<MetricResponse> findByServiceNameAndTimeRange(
            String serviceName, Instant from, Instant to, int page, int size) {
        String uri = UriComponentsBuilder
                .fromUriString(metricsUrl + "/api/metrics/service/{serviceName}/range")
                .queryParam("from", from.toString())
                .queryParam("to", to.toString())
                .queryParam("page", page)
                .queryParam("size", size)
                .buildAndExpand(serviceName)
                .toUriString();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

}
