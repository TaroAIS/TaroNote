package com.taronote.common.port;

import java.util.Map;

// 日志能力端口：统一结构化日志出口。
public interface LoggingPort {
    void info(String event, Map<String, Object> fields);

    void warn(String event, Map<String, Object> fields);

    void error(String event, Map<String, Object> fields, Throwable error);
}
