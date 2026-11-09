package com.project.minibank.customer.application.port.in;

import com.project.minibank.customer.domain.model.Favorite;

public interface UpdateFavoriteUseCase {
    Favorite updateFavorite(UpdateFavoriteCommand command);
}
