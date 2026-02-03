package com.taronote.common.security;

import java.util.UUID;

public record UserPrincipal(UUID id, String username, String type) {
}
