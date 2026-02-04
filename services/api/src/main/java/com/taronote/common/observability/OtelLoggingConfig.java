package com.taronote.common.observability;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.resources.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// OTel 日志配置：提供可切换的 OTLP 导出。
@Configuration
@ConditionalOnProperty(name = "taronote.logging.mode", havingValue = "otel")
public class OtelLoggingConfig {
    @Bean(destroyMethod = "close")
    public SdkLoggerProvider loggerProvider(
            @Value("${spring.application.name:taronote-api}") String serviceName,
            @Value("${taronote.logging.otlp-endpoint:}") String endpoint,
            @Value("${taronote.logging.otlp-protocol:}") String protocol) {
        Resource resource = Resource.getDefault().merge(Resource.create(Attributes.of(
                AttributeKey.stringKey("service.name"), serviceName
        )));
        LogRecordExporter exporter = buildExporter(endpoint);
        return SdkLoggerProvider.builder()
                .setResource(resource)
                .addLogRecordProcessor(BatchLogRecordProcessor.builder(exporter).build())
                .build();
    }

    @Bean
    public OpenTelemetry openTelemetry(SdkLoggerProvider loggerProvider) {
        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
                .setLoggerProvider(loggerProvider)
                .build();
        GlobalOpenTelemetry.set(sdk);
        return sdk;
    }

    private LogRecordExporter buildExporter(String endpoint) {
        String resolvedEndpoint = resolveEndpoint(endpoint);
        var builder = OtlpGrpcLogRecordExporter.builder();
        if (!resolvedEndpoint.isBlank()) {
            builder.setEndpoint(resolvedEndpoint);
        }
        return builder.build();
    }

    private String resolveEndpoint(String endpoint) {
        if (endpoint != null && !endpoint.isBlank()) {
            return endpoint.trim();
        }
        String env = System.getenv("OTEL_EXPORTER_OTLP_ENDPOINT");
        return env == null ? "" : env.trim();
    }
}
