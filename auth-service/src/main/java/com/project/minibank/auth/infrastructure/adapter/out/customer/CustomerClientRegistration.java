package com.project.minibank.auth.infrastructure.adapter.out.customer;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = CustomerClient.class)
class CustomerClientRegistration {
}
