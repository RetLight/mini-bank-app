package com.project.minibank.auth.infrastructure.adapter.in.web;

import com.project.minibank.auth.application.port.in.LoginCommand;
import com.project.minibank.auth.domain.model.AccessToken;

final class AuthWebMapper {

    private AuthWebMapper() {
    }

    static LoginCommand toCommand(LoginRequest request) {
        return new LoginCommand(request.getUsername(), request.getPassword());
    }

    static TokenResponse toResponse(AccessToken token) {
        return new TokenResponse(token.value(), token.type(), token.expiresInSeconds());
    }
}
