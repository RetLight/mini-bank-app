package com.project.minibank.customer.infrastructure.adapter.out.persistence;

import com.project.minibank.customer.domain.model.Customer;
import com.project.minibank.customer.domain.model.Favorite;

final class FavoritePersistenceMapper {

    private FavoritePersistenceMapper() {
    }

    static Favorite toDomain(FavoriteEntity entity) {
        return new Favorite(
                entity.getId(),
                entity.getAlias(),
                entity.getAccountNumber(),
                entity.getBank(),
                entity.getHolder(),
                entity.getCustomer().getId(),
                entity.isOwnerVerified()
        );
    }

    static FavoriteEntity toEntity(Favorite favorite, CustomerEntity customer) {
        FavoriteEntity entity = new FavoriteEntity(favorite.getAlias(), favorite.getAccountNumber(),
                favorite.getBank(), favorite.getHolder(), customer);
        entity.setId(favorite.getId());
        entity.setOwnerVerified(favorite.isOwnerVerified());
        return entity;
    }

    static Customer toDomain(CustomerEntity entity) {
        return new Customer(
                entity.getId(),
                entity.getDocumentType(),
                entity.getDocumentNumber(),
                entity.getFirstNames(),
                entity.getLastNames(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getStatus(),
                entity.getRegisteredAt(),
                entity.getFavorites().stream().map(FavoritePersistenceMapper::toDomain).toList()
        );
    }
}
