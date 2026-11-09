package com.project.minibank.customer.application.port.in;

public record AddFavoriteCommand(Long customerId, String alias, String accountNumber, String bank, String holder) {
}
