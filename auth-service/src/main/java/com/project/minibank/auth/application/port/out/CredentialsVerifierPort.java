package com.project.minibank.auth.application.port.out;

public interface CredentialsVerifierPort {

    void verify(String username, String password);
}
