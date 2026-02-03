package com.taronote.admin.web;

import jakarta.validation.constraints.NotBlank;

public record InjectRequest(
        @NotBlank String directive
) {
}
