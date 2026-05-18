package com.monitoring.ingestion.forwarder;

import com.monitoring.ingestion.dto.BatchIngestResponse;
import com.monitoring.ingestion.dto.LogRequest;
import com.monitoring.ingestion.dto.LogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LogsForwarder {

    private final RestClient restClient;

    @Value("${services.logs-url}")
    private String logsUrl;

    public LogResponse forward(LogRequest request) {
        return restClient.post()
                .uri(logsUrl + "/api/logs")
                .body(request)
                .retrieve()
                .body(LogResponse.class);
    }

    public BatchIngestResponse forwardBatch(List<LogRequest> requests) {
        return restClient.post()
                .uri(logsUrl + "/api/logs/batch")
                .body(requests)
                .retrieve()
                .body(BatchIngestResponse.class);
    }
}
