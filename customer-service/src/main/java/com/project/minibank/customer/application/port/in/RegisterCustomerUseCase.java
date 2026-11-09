package com.project.minibank.customer.application.port.in;

import com.project.minibank.customer.domain.model.Customer;

public interface RegisterCustomerUseCase {
    Customer register(RegisterCustomerCommand command);
}
