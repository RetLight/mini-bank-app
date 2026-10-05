package com.project.minibank.customer.infrastructure.adapter.in.web;

import com.project.minibank.customer.application.port.in.AddFavoriteUseCase;
import com.project.minibank.customer.application.port.in.GetFavoriteUseCase;
import com.project.minibank.customer.domain.model.Favorite;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/me/favorites")
public class FavoriteController {

    private final AddFavoriteUseCase addFavoriteUseCase;
    private final GetFavoriteUseCase getFavoriteUseCase;

    public FavoriteController(AddFavoriteUseCase addFavoriteUseCase,
                              GetFavoriteUseCase getFavoriteUseCase) {
        this.addFavoriteUseCase = addFavoriteUseCase;
        this.getFavoriteUseCase = getFavoriteUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FavoriteResponse add(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody FavoriteRequest request) {
        Favorite favorite = addFavoriteUseCase.addFavorite(FavoriteWebMapper.toCommand(customerId(jwt), request));
        return FavoriteWebMapper.toResponse(favorite);
    }

    @GetMapping
    public List<FavoriteResponse> findAll(@AuthenticationPrincipal Jwt jwt) {
        return FavoriteWebMapper.toResponse(getFavoriteUseCase.findAll(customerId(jwt)));
    }

    @GetMapping("/{favoriteId}")
    public FavoriteResponse findById(@AuthenticationPrincipal Jwt jwt, @PathVariable Long favoriteId) {
        return FavoriteWebMapper.toResponse(getFavoriteUseCase.findById(customerId(jwt), favoriteId));
    }

    @GetMapping("/alias/{alias}")
    public FavoriteResponse findByAlias(@AuthenticationPrincipal Jwt jwt, @PathVariable String alias) {
        return FavoriteWebMapper.toResponse(getFavoriteUseCase.findByAlias(customerId(jwt), alias));
    }

    private static Long customerId(Jwt jwt) {
        return Long.valueOf(jwt.getClaimAsString("customerId"));
    }
}
