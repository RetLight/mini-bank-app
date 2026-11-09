package com.project.minibank.auth.infrastructure.adapter.in.web;

import com.project.minibank.auth.application.port.in.LoginCommand;
import com.project.minibank.auth.application.port.in.RegisterCommand;
import com.project.minibank.auth.domain.model.AccessToken;

final class AuthWebMapper {

    private AuthWebMapper() {
    }

    static LoginCommand toCommand(LoginRequest request) {
        return new LoginCommand(request.getUsername(), request.getPassword());
    }

    static RegisterCommand toCommand(RegisterRequest request) {
        return new RegisterCommand(request.username(), request.password(), request.documentType(),
                request.documentNumber(), request.firstNames(), request.lastNames(), request.email(), request.phone());
    }

    static TokenResponse toResponse(AccessToken token) {
        return new TokenResponse(token.value(), token.type(), token.expiresInSeconds());
    }
}
