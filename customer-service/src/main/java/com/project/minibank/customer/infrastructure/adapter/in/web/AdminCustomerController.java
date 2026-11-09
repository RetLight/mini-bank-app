package com.project.minibank.customer.infrastructure.adapter.in.web;

import com.project.minibank.customer.application.port.in.ListCustomersUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Solo ADMIN: lo exige la regla /admin/** de SecurityConfig. Un cliente con token válido recibe 403.
@RestController
@RequestMapping("/admin/customers")
public class AdminCustomerController {

    private final ListCustomersUseCase listCustomersUseCase;

    public AdminCustomerController(ListCustomersUseCase listCustomersUseCase) {
        this.listCustomersUseCase = listCustomersUseCase;
    }

    @GetMapping
    public List<CustomerResponse> findAll() {
        return listCustomersUseCase.findAll().stream().map(CustomerResponse::from).toList();
    }
}
