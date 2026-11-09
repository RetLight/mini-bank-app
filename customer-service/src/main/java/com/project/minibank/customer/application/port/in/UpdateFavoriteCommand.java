package com.project.minibank.customer.application.port.in;

public record UpdateFavoriteCommand(Long customerId, Long favoriteId, String alias, String accountNumber, String bank,
                                    String holder) {
}
