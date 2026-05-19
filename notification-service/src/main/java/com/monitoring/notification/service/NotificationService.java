package com.monitoring.notification.service;

import com.monitoring.notification.dto.AlertRuleRequest;
import com.monitoring.notification.dto.AlertRuleResponse;
import com.monitoring.notification.dto.MetricMessage;
import com.monitoring.notification.dto.NotificationResponse;
import com.monitoring.notification.entity.AlertRule;
import com.monitoring.notification.entity.Notification;
import com.monitoring.notification.repo.AlertRuleRepository;
import com.monitoring.notification.repo.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final AlertRuleRepository alertRuleRepository;
    private final NotificationRepository notificationRepository;

    @RabbitListener(queues = "${rabbitmq.queue}")
    @Transactional
    public void onMetricIngested(MetricMessage metric) {
        log.info("Received metric event: {} from {}", metric.name(), metric.serviceName());
        alertRuleRepository.findByMetricNameAndServiceName(metric.name(), metric.serviceName())
                .forEach(rule -> {
                    if (isThresholdBreached(rule, metric.value()) && !isInCooldown(rule)) {
                        Notification notification = Notification.builder()
                                .alertRule(rule)
                                .triggerValue(metric.value())
                                .firedAt(Instant.now())
                                .build();
                        notificationRepository.save(notification);
                        log.info("Notification fired for rule id: {}", rule.getId());
                    }
                });
    }

    @Transactional
    public AlertRuleResponse createAlertRule(AlertRuleRequest request) {
        AlertRule rule = AlertRule.builder()
                .metricName(request.metricName())
                .serviceName(request.serviceName())
                .threshold(request.threshold())
                .direction(request.direction())
                .cooldownSeconds(request.cooldownSeconds())
                .build();
        return toAlertRuleResponse(alertRuleRepository.save(rule));
    }

    @Transactional(readOnly = true)
    public Page<AlertRuleResponse> findAllAlertRules(Pageable pageable) {
        return alertRuleRepository.findAll(pageable).map(this::toAlertRuleResponse);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> findAllNotifications(Pageable pageable) {
        return notificationRepository.findAll(pageable).map(this::toNotificationResponse);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> findNotificationsByAlertRule(Long alertRuleId, Pageable pageable) {
        return notificationRepository.findByAlertRuleId(alertRuleId, pageable).map(this::toNotificationResponse);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> findNotificationsByTimeRange(Instant from, Instant to, Pageable pageable) {
        return notificationRepository.findByFiredAtBetween(from, to, pageable).map(this::toNotificationResponse);
    }

    private boolean isThresholdBreached(AlertRule rule, Double value) {
        return switch (rule.getDirection()) {
            case ABOVE -> value > rule.getThreshold();
            case BELOW -> value < rule.getThreshold();
        };
    }

    private boolean isInCooldown(AlertRule rule) {
        return notificationRepository.findTopByAlertRuleIdOrderByFiredAtDesc(rule.getId())
                .map(last -> last.getFiredAt()
                        .plusSeconds(rule.getCooldownSeconds())
                        .isAfter(Instant.now()))
                .orElse(false);
    }

    private AlertRuleResponse toAlertRuleResponse(AlertRule rule) {
        return AlertRuleResponse.builder()
                .id(rule.getId())
                .metricName(rule.getMetricName())
                .serviceName(rule.getServiceName())
                .threshold(rule.getThreshold())
                .direction(rule.getDirection())
                .cooldownSeconds(rule.getCooldownSeconds())
                .build();
    }

    private NotificationResponse toNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .alertRule(toAlertRuleResponse(notification.getAlertRule()))
                .triggerValue(notification.getTriggerValue())
                .firedAt(notification.getFiredAt())
                .build();
    }
}
