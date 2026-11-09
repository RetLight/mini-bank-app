package com.project.minibank.customer.infrastructure.adapter.in.web;

import com.project.minibank.customer.application.port.in.RegisterCustomerCommand;
import com.project.minibank.customer.application.port.in.RegisterCustomerUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint interno: solo lo llama auth-service durante el registro, con un token de servicio (rol SERVICE).
 * El gateway no tiene ruta hacia /internal/**, y SecurityConfig rechaza con 403 cualquier otro token.
 */
@RestController
@RequestMapping("/internal/customers")
public class InternalCustomerController {

    public record CreateCustomerRequest(
            @NotBlank @Size(max = 10) String documentType,
            @NotBlank @Pattern(regexp = "\\d{8,20}") String documentNumber,
            @NotBlank @Size(max = 100) String firstNames,
            @NotBlank @Size(max = 100) String lastNames,
            @NotBlank @Email @Size(max = 150) String email,
            @Pattern(regexp = "\\d{6,20}") String phone) {
    }

    public record CreatedCustomerResponse(Long id) {
    }

    private final RegisterCustomerUseCase registerCustomerUseCase;

    public InternalCustomerController(RegisterCustomerUseCase registerCustomerUseCase) {
        this.registerCustomerUseCase = registerCustomerUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatedCustomerResponse create(@Valid @RequestBody CreateCustomerRequest request) {
        var customer = registerCustomerUseCase.register(new RegisterCustomerCommand(request.documentType(),
                request.documentNumber(), request.firstNames(), request.lastNames(), request.email(), request.phone()));
        return new CreatedCustomerResponse(customer.getId());
    }
}
