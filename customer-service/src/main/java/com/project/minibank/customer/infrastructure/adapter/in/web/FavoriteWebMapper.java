package com.project.minibank.customer.infrastructure.adapter.in.web;

import com.project.minibank.customer.application.port.in.AddFavoriteCommand;
import com.project.minibank.customer.application.port.in.UpdateFavoriteCommand;
import com.project.minibank.customer.domain.model.Favorite;

import java.util.List;

final class FavoriteWebMapper {

    private FavoriteWebMapper() {
    }

    static AddFavoriteCommand toCommand(Long customerId, FavoriteRequest request) {
        return new AddFavoriteCommand(
                customerId,
                request.getAlias(),
                request.getAccountNumber(),
                request.getBank(),
                request.getHolder()
        );
    }

    static UpdateFavoriteCommand toCommand(Long customerId, Long favoriteId, FavoriteRequest request) {
        return new UpdateFavoriteCommand(
                customerId,
                favoriteId,
                request.getAlias(),
                request.getAccountNumber(),
                request.getBank(),
                request.getHolder()
        );
    }

    static FavoriteResponse toResponse(Favorite favorite) {
        return new FavoriteResponse(
                favorite.getId(),
                favorite.getAlias(),
                favorite.getAccountNumber(),
                favorite.getBank(),
                favorite.getHolder(),
                favorite.isOwnerVerified()
        );
    }

    static List<FavoriteResponse> toResponse(List<Favorite> favorites) {
        return favorites.stream()
                .map(FavoriteWebMapper::toResponse)
                .toList();
    }
}
