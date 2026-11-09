package com.project.minibank.customer.infrastructure.adapter.in.web;

import com.project.minibank.common.security.JwtPrincipal;
import com.project.minibank.customer.application.port.in.AddFavoriteUseCase;
import com.project.minibank.customer.application.port.in.DeleteFavoriteUseCase;
import com.project.minibank.customer.application.port.in.GetFavoriteUseCase;
import com.project.minibank.customer.application.port.in.UpdateFavoriteUseCase;
import com.project.minibank.customer.domain.model.Favorite;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final AddFavoriteUseCase addFavoriteUseCase;
    private final GetFavoriteUseCase getFavoriteUseCase;
    private final UpdateFavoriteUseCase updateFavoriteUseCase;
    private final DeleteFavoriteUseCase deleteFavoriteUseCase;

    public FavoriteController(AddFavoriteUseCase addFavoriteUseCase,
                              GetFavoriteUseCase getFavoriteUseCase,
                              UpdateFavoriteUseCase updateFavoriteUseCase,
                              DeleteFavoriteUseCase deleteFavoriteUseCase) {
        this.addFavoriteUseCase = addFavoriteUseCase;
        this.getFavoriteUseCase = getFavoriteUseCase;
        this.updateFavoriteUseCase = updateFavoriteUseCase;
        this.deleteFavoriteUseCase = deleteFavoriteUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FavoriteResponse add(@AuthenticationPrincipal JwtPrincipal principal, @Valid @RequestBody FavoriteRequest request) {
        Favorite favorite = addFavoriteUseCase.addFavorite(FavoriteWebMapper.toCommand(principal.customerId(), request));
        return FavoriteWebMapper.toResponse(favorite);
    }

    @GetMapping
    public List<FavoriteResponse> findAll(@AuthenticationPrincipal JwtPrincipal principal) {
        return FavoriteWebMapper.toResponse(getFavoriteUseCase.findAll(principal.customerId()));
    }

    @GetMapping("/{favoriteId}")
    public FavoriteResponse findById(@AuthenticationPrincipal JwtPrincipal principal, @PathVariable Long favoriteId) {
        return FavoriteWebMapper.toResponse(getFavoriteUseCase.findById(principal.customerId(), favoriteId));
    }

    @GetMapping("/alias/{alias}")
    public FavoriteResponse findByAlias(@AuthenticationPrincipal JwtPrincipal principal, @PathVariable String alias) {
        return FavoriteWebMapper.toResponse(getFavoriteUseCase.findByAlias(principal.customerId(), alias));
    }

    // Solo el dueño: el cliente sale del token, y el favorito de otro cliente responde 404 (no se confirma que exista).
    @PutMapping("/{favoriteId}")
    public FavoriteResponse update(@AuthenticationPrincipal JwtPrincipal principal, @PathVariable Long favoriteId,
                                   @Valid @RequestBody FavoriteRequest request) {
        Favorite favorite = updateFavoriteUseCase.updateFavorite(FavoriteWebMapper.toCommand(principal.customerId(), favoriteId, request));
        return FavoriteWebMapper.toResponse(favorite);
    }

    @DeleteMapping("/{favoriteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal JwtPrincipal principal, @PathVariable Long favoriteId) {
        deleteFavoriteUseCase.deleteFavorite(principal.customerId(), favoriteId);
    }

}
