package com.project.minibank.auth.infrastructure.adapter.out.customer;

import com.project.minibank.auth.application.port.out.CustomerProvisioningPort;
import com.project.minibank.auth.application.port.out.NewCustomer;
import com.project.minibank.auth.domain.exception.CustomerServiceUnavailableException;
import feign.FeignException;
import org.springframework.stereotype.Component;

// Crea el cliente en customer-service por HTTP. Un 409 llega como CustomerAlreadyExistsException (lo traduce el
// ErrorDecoder); cualquier otro fallo, incluidos conexión rechazada y timeout, significa "no se pudo crear".
@Component
class CustomerProvisioningFeignAdapter implements CustomerProvisioningPort {

    private final CustomerClient customerClient;

    CustomerProvisioningFeignAdapter(CustomerClient customerClient) {
        this.customerClient = customerClient;
    }

    @Override
    public Long create(NewCustomer customer) {
        try {
            return customerClient.create(new CustomerClient.CreateCustomerRequest(customer.documentType(),
                    customer.documentNumber(), customer.firstNames(), customer.lastNames(), customer.email(),
                    customer.phone())).id();
        } catch (CustomerClientConfig.CustomerCallFailedException | FeignException e) {
            throw new CustomerServiceUnavailableException("No se pudo crear el cliente: customer-service no está disponible", e);
        }
    }
}
