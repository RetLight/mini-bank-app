package com.project.minibank.auth.application.port.in;

import com.project.minibank.auth.domain.model.AccessToken;

public interface RegisterUseCase {
    AccessToken register(RegisterCommand command);
}
