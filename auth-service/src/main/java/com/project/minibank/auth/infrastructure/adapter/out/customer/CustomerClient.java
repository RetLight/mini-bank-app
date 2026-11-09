package com.project.minibank.auth.infrastructure.adapter.out.customer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Cliente HTTP declarativo. Vive en infraestructura, detrás de CustomerProvisioningPort: la aplicación no conoce Feign.
// Sin service discovery: la URL sale de la configuración (config-repo).
@FeignClient(name = "customer-service", url = "${minibank.services.customer-url}", configuration = CustomerClientConfig.class)
interface CustomerClient {

    record CreateCustomerRequest(String documentType, String documentNumber, String firstNames, String lastNames,
                                 String email, String phone) {
    }

    record CreatedCustomerResponse(Long id) {
    }

    @PostMapping("/internal/customers")
    CreatedCustomerResponse create(@RequestBody CreateCustomerRequest request);
}
