package com.project.minibank.customer.application.port.in;

public interface DeleteFavoriteUseCase {
    void deleteFavorite(Long customerId, Long favoriteId);
}
