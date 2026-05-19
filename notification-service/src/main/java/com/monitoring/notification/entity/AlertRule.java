package com.monitoring.notification.entity;


import com.monitoring.notification.enums.ThresholdDirection;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "alert_rules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String metricName;

    @NotBlank
    @Column(nullable = false)
    private String serviceName;

    @NotNull
    @Column(nullable = false)
    private Double threshold;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ThresholdDirection direction;

    @NotNull
    @Column(nullable = false)
    private Long cooldownSeconds;
}
