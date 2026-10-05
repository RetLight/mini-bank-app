package com.project.minibank.customer.application.port.out;

import com.project.minibank.customer.domain.model.Favorite;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepositoryPort {

    Favorite save(Favorite favorite);

    Optional<Favorite> findById(Long customerId, Long favoriteId);

    List<Favorite> findAll(Long customerId);

    Optional<Favorite> findByAlias(Long customerId, String alias);
}
