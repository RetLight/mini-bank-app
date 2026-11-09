package com.project.minibank.movement.infrastructure.adapter.in.web;

import com.project.minibank.common.security.JwtPrincipal;
import com.project.minibank.movement.application.port.in.GetMovementUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/movements")
public class MovementController {

    private final GetMovementUseCase getMovementUseCase;

    public MovementController(GetMovementUseCase getMovementUseCase) {
        this.getMovementUseCase = getMovementUseCase;
    }

    @GetMapping("/{id}")
    public MovementResponse findById(@AuthenticationPrincipal JwtPrincipal principal, @PathVariable Long id) {
        return MovementWebMapper.toResponse(getMovementUseCase.findById(principal.customerId(), id));
    }

    @GetMapping
    public List<MovementResponse> findAllByAccount(@AuthenticationPrincipal JwtPrincipal principal, @RequestParam Long accountId) {
        return MovementWebMapper.toResponse(getMovementUseCase.findAllByAccount(principal.customerId(), accountId));
    }

}
