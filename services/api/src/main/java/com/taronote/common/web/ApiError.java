package com.taronote.common.web;

import java.time.Instant;

public record ApiError(
        String message,
        String path,
        Instant timestamp
) {
}
