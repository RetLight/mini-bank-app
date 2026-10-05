package com.project.minibank.movement.infrastructure.adapter.in.web;

import com.project.minibank.movement.application.port.in.GetMovementUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/me/movements")
public class MovementController {

    private final GetMovementUseCase getMovementUseCase;

    public MovementController(GetMovementUseCase getMovementUseCase) {
        this.getMovementUseCase = getMovementUseCase;
    }

    @GetMapping("/{id}")
    public MovementResponse findById(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return MovementWebMapper.toResponse(getMovementUseCase.findById(customerId(jwt), id));
    }

    @GetMapping
    public List<MovementResponse> findAllByAccount(@AuthenticationPrincipal Jwt jwt, @RequestParam Long accountId) {
        return MovementWebMapper.toResponse(getMovementUseCase.findAllByAccount(customerId(jwt), accountId));
    }

    private static Long customerId(Jwt jwt) {
        return Long.valueOf(jwt.getClaimAsString("customerId"));
    }
}
