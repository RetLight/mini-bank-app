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
        Transfer transfer = new Transfer(
                null,
                cmd.amount(),
                source.getCurrency(),
                "COMPLETED",
                null,
                now,
                now,
                destination.isPresent()?"INTERNAL":"EXTERNAL",
                cmd.destinationCci(),
                cmd.destinationBank(),
                cmd.destinationHolder(),
                cmd.sourceAccountId(),
                destination.isPresent()? destination.get().getId() : null
        );

        Transfer saved = transferRepositoryPort.save(transfer);

        Movement movement = new Movement(
                null,
                "DECREASE",
                transfer.getAmount(),
                source.getBalance(),
                now,
                source.getId(),
                saved.getId()
        );

        movementRepositoryPort.save(movement);
        accountRepositoryPort.saveBalance(source);

        Account accountDestination = null;
        if(destination.isPresent()){
            accountDestination = destination.get();
            accountDestination = accountRepositoryPort.saveBalance(accountDestination);

            movementRepositoryPort.save(new Movement(
                    null,
                    "INCREASE",
                    movement.getAmount(),
                    accountDestination.getBalance(),
                    now,
                    accountDestination.getId(),
                    saved.getId()
            ));
        }

        return saved;
    }
}
