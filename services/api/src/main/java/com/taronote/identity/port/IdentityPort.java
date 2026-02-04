package com.taronote.identity.port;

import com.taronote.identity.web.AuthResponse;
import com.taronote.identity.web.LoginRequest;
import com.taronote.identity.web.RegisterRequest;

// 身份能力端口：统一认证与注册能力。
public interface IdentityPort {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
