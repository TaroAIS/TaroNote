package com.taronote.common.observability;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

// 安装 Logback OTel Appender，绑定 OpenTelemetry 实例。
@Component
@ConditionalOnBean(OpenTelemetry.class)
public class OtelLogbackInstaller {
    private final OpenTelemetry openTelemetry;

    public OtelLogbackInstaller(OpenTelemetry openTelemetry) {
        this.openTelemetry = openTelemetry;
    }

    @PostConstruct
    public void install() {
        OpenTelemetryAppender.install(openTelemetry);
    }
}
