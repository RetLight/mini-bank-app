package com.project.minibank.customer.infrastructure.adapter.out.auth;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = AuthClient.class)
class AuthClientRegistration {
}
