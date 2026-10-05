package com.project.minibank.customer.application.port.in;

import com.project.minibank.customer.domain.model.Favorite;

import java.util.List;

public interface GetFavoriteUseCase {

    Favorite findById(Long customerId, Long favoriteId);

    Favorite findByAlias(Long customerId, String alias);

    List<Favorite> findAll(Long customerId);
}
