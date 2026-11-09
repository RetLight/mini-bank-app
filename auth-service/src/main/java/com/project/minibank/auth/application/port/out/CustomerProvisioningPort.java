package com.project.minibank.auth.application.port.out;

/** Crea el cliente y devuelve su id, sin saber si eso ocurre en este proceso o en otro servicio por HTTP. */
public interface CustomerProvisioningPort {
    Long create(NewCustomer customer);
}
