package com.monitoring.metrics.event;

import com.monitoring.metrics.dto.MetricResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    public void publish(MetricResponse metricResponse) {
        log.info("Publishing metric event for service: {}", metricResponse.serviceName());
        rabbitTemplate.convertAndSend(exchange, routingKey, metricResponse);
    }
}
