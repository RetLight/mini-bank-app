package com.project.minibank.auth.application.port.in;

import com.project.minibank.auth.domain.model.AccessToken;

public interface LoginUseCase {

    AccessToken login(LoginCommand command);
}
