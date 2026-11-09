package com.project.minibank.auth.application.port.in;

import com.project.minibank.auth.domain.model.User;

public interface GetMyUserUseCase {
    User me(String username);
}
