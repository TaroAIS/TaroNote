package com.taronote.identity.adapter;

import com.taronote.identity.port.IdentityPort;
import com.taronote.identity.service.AuthService;
import com.taronote.identity.web.AuthResponse;
import com.taronote.identity.web.LoginRequest;
import com.taronote.identity.web.RegisterRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// 身份适配器：把端口调用翻译到当前实现。
@Service
@ConditionalOnProperty(name = "taronote.adapter.mode", havingValue = "default", matchIfMissing = true)
public class IdentityAdapter implements IdentityPort {
    private final AuthService authService;

    public IdentityAdapter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        return authService.register(request);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return authService.login(request);
    }
}
