package com.project.minibank.movement.infrastructure.adapter.in.web;

import com.project.minibank.common.security.JwtPrincipal;
import com.project.minibank.movement.application.port.in.GetTransferUseCase;
import com.project.minibank.movement.application.port.in.MakeTransferUseCase;
import com.project.minibank.movement.domain.model.Transfer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final MakeTransferUseCase makeTransferUseCase;
    private final GetTransferUseCase getTransferUseCase;

    public TransferController(MakeTransferUseCase makeTransferUseCase,
                              GetTransferUseCase getTransferUseCase) {
        this.makeTransferUseCase = makeTransferUseCase;
        this.getTransferUseCase = getTransferUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransferResponse makeTransfer(@AuthenticationPrincipal JwtPrincipal principal, @Valid @RequestBody TransferRequest request) {
        Transfer transfer = makeTransferUseCase.makeTransfer(TransferWebMapper.toCommand(principal.customerId(), request));
        return TransferWebMapper.toResponse(transfer);
    }

    @GetMapping("/{id}")
    public TransferResponse findById(@AuthenticationPrincipal JwtPrincipal principal, @PathVariable Long id) {
        return TransferWebMapper.toResponse(getTransferUseCase.findById(principal.customerId(), id));
    }

    @GetMapping
    public List<TransferResponse> findAllByAccount(@AuthenticationPrincipal JwtPrincipal principal, @RequestParam Long accountId) {
        return TransferWebMapper.toResponse(getTransferUseCase.findAllByAccount(principal.customerId(), accountId));
    }

}
