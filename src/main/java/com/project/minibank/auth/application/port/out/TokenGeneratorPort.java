package com.project.minibank.auth.application.port.out;

import com.project.minibank.auth.domain.model.AccessToken;
import com.project.minibank.auth.domain.model.User;

public interface TokenGeneratorPort {

    AccessToken generate(User user);
}
