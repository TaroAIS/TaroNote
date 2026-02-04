package com.taronote.common.adapter;

import com.taronote.common.port.LoggingPort;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// 默认日志适配器：输出到 SLF4J/控制台。
@Service
@ConditionalOnProperty(name = "taronote.logging.mode", havingValue = "slf4j", matchIfMissing = true)
public class Slf4jLoggingAdapter implements LoggingPort {
    private static final Logger log = LoggerFactory.getLogger(Slf4jLoggingAdapter.class);

    @Override
    public void info(String event, Map<String, Object> fields) {
        log.info(format(event, fields));
    }

    @Override
    public void warn(String event, Map<String, Object> fields) {
        log.warn(format(event, fields));
    }

    @Override
    public void error(String event, Map<String, Object> fields, Throwable error) {
        log.error(format(event, fields), error);
    }

    private String format(String event, Map<String, Object> fields) {
        StringBuilder builder = new StringBuilder();
        builder.append("event=").append(event == null ? "unknown" : event);
        if (fields == null || fields.isEmpty()) {
            return builder.toString();
        }
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            builder.append(' ')
                    .append(entry.getKey())
                    .append('=')
                    .append(entry.getValue());
        }
        return builder.toString();
    }
}
