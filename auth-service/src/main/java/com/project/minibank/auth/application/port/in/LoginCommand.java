package com.project.minibank.auth.application.port.in;

public record LoginCommand(String username, String password) {
}
