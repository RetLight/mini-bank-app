package com.project.minibank.movement.application.service;

import com.project.minibank.movement.application.port.in.GetTransferUseCase;
import com.project.minibank.movement.application.port.in.MakeTransferCommand;
import com.project.minibank.movement.application.port.in.MakeTransferUseCase;
import com.project.minibank.movement.application.port.out.AccountRepositoryPort;
import com.project.minibank.movement.application.port.out.MovementRepositoryPort;
import com.project.minibank.movement.application.port.out.TransferRepositoryPort;
import com.project.minibank.movement.domain.exception.*;
import com.project.minibank.movement.domain.model.Account;
import com.project.minibank.movement.domain.model.Movement;
import com.project.minibank.movement.domain.model.Transfer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransferService implements MakeTransferUseCase, GetTransferUseCase {
    private final TransferRepositoryPort transferRepositoryPort;
    private final MovementRepositoryPort movementRepositoryPort;
    private final AccountRepositoryPort accountRepositoryPort;

    public TransferService(TransferRepositoryPort transferRepositoryPort, MovementRepositoryPort movementRepositoryPort, AccountRepositoryPort accountRepositoryPort) {
        this.transferRepositoryPort = transferRepositoryPort;
        this.movementRepositoryPort = movementRepositoryPort;
        this.accountRepositoryPort = accountRepositoryPort;
    }


    @Override
    public Transfer findById(Long customerId, Long id) {
        return transferRepositoryPort.findById(customerId,id)
                .orElseThrow(() -> new TransferNotFoundException(customerId, id));
    }

    @Override
    public List<Transfer> findAllByAccount(Long customerId, Long accountId) {
        return transferRepositoryPort.findAllByAccount(customerId, accountId);
    }

    @Override
    @Transactional
    public Transfer makeTransfer(MakeTransferCommand cmd) {
        Account source = accountRepositoryPort.findById(cmd.customerId(),cmd.sourceAccountId())
                .filter(account1 -> account1.belongsTo(cmd.customerId()))
                .orElseThrow(() -> new AccountNotFoundException(cmd.customerId(),cmd.sourceAccountId()));

        if (source.getCci().equals(cmd.destinationCci())) {
            throw new SameAccountTransferException(source.getId());
        }

        Optional<Account> destination = accountRepositoryPort.findByCci(cmd.destinationCci());

        if (destination.isPresent() && !destination.get().getCurrency().equals(source.getCurrency())) {
            throw new CurrencyMismatchException(source.getCurrency(), destination.get().getCurrency());
        }

        source.decrease(cmd.amount());
        destination.ifPresent(account2 -> account2.increase(cmd.amount()));

        LocalDateTime now = LocalDateTime.now();
        Transfer saved = transferRepositoryPort.save(Transfer.create(
                source,
                destination.orElse(null),
                cmd.amount(),
                cmd.destinationCci(),
                cmd.destinationBank(),
                cmd.destinationHolder(),
                now
        ));

        movementRepositoryPort.save(Movement.debit(source, cmd.amount(), saved.getId(), now));
        accountRepositoryPort.saveBalance(source);

        destination.ifPresent(account -> {
            Account savedDestination = accountRepositoryPort.saveBalance(account);
            movementRepositoryPort.save(Movement.credit(savedDestination, cmd.amount(), saved.getId(), now));
        });

        return saved;
    }
}
