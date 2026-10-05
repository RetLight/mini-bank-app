package com.project.minibank.movement.application.service;

import com.project.minibank.movement.application.port.in.MakeTransferCommand;
import com.project.minibank.movement.application.port.out.AccountRepositoryPort;
import com.project.minibank.movement.application.port.out.MovementRepositoryPort;
import com.project.minibank.movement.application.port.out.TransferRepositoryPort;
import com.project.minibank.movement.domain.exception.AccountNotFoundException;
import com.project.minibank.movement.domain.exception.CurrencyMismatchException;
import com.project.minibank.movement.domain.exception.InsufficientAccountBalanceException;
import com.project.minibank.movement.domain.exception.SameAccountTransferException;
import com.project.minibank.movement.domain.exception.TransferNotFoundException;
import com.project.minibank.movement.domain.model.Account;
import com.project.minibank.movement.domain.model.Movement;
import com.project.minibank.movement.domain.model.Transfer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @Mock
    TransferRepositoryPort transferRepositoryPort;

    @Mock
    MovementRepositoryPort movementRepositoryPort;

    @Mock
    AccountRepositoryPort accountRepositoryPort;

    @InjectMocks
    TransferService transferService;

    @Test
    void shouldThrowWhenSourceAccountNotExists() {
        when(accountRepositoryPort.findById(10L, 99L))
                .thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> transferService.makeTransfer(command(10L, 99L, "100", "00219100000000000002")));
        verify(transferRepositoryPort, never()).save(any(Transfer.class));
    }

    @Test
    void shouldThrowWhenTransferToSameAccount() {
        Account source = account(1L, "00219100000000000001", "PEN", "1000.00", 10L);
        when(accountRepositoryPort.findById(10L, 1L))
                .thenReturn(Optional.of(source));

        assertThrows(SameAccountTransferException.class,
                () -> transferService.makeTransfer(command(10L, 1L, "100", "00219100000000000001")));
        verify(transferRepositoryPort, never()).save(any(Transfer.class));
    }

    @Test
    void shouldThrowWhenCurrencyIsDifferent() {
        Account source = account(1L, "00219100000000000001", "PEN", "1000.00", 10L);
        Account destination = account(2L, "00219100000000000002", "USD", "500.00", 20L);
        when(accountRepositoryPort.findById(10L, 1L))
                .thenReturn(Optional.of(source));
        when(accountRepositoryPort.findByCci("00219100000000000002"))
                .thenReturn(Optional.of(destination));

        assertThrows(CurrencyMismatchException.class,
                () -> transferService.makeTransfer(command(10L, 1L, "100", "00219100000000000002")));
        verify(transferRepositoryPort, never()).save(any(Transfer.class));
    }

    @Test
    void shouldThrowWhenBalanceIsInsufficient() {
        Account source = account(1L, "00219100000000000001", "PEN", "50.00", 10L);
        when(accountRepositoryPort.findById(10L, 1L))
                .thenReturn(Optional.of(source));
        when(accountRepositoryPort.findByCci("00311122233344455566"))
                .thenReturn(Optional.empty());

        assertThrows(InsufficientAccountBalanceException.class,
                () -> transferService.makeTransfer(command(10L, 1L, "100", "00311122233344455566")));
        verify(accountRepositoryPort, never()).saveBalance(any(Account.class));
    }

    @Test
    void shouldSaveTransferAndMovementsForInternalTransfer() {
        Account source = account(1L, "00219100000000000001", "PEN", "1000.00", 10L);
        Account destination = account(2L, "00219100000000000002", "PEN", "500.00", 20L);
        when(accountRepositoryPort.findById(10L, 1L))
                .thenReturn(Optional.of(source));
        when(accountRepositoryPort.findByCci("00219100000000000002"))
                .thenReturn(Optional.of(destination));
        when(transferRepositoryPort.save(any(Transfer.class)))
                .thenAnswer(invocation -> withId(invocation.getArgument(0), 1L));
        when(accountRepositoryPort.saveBalance(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transfer valueReturned = transferService.makeTransfer(command(10L, 1L, "100", "00219100000000000002"));

        assertEquals(1L, valueReturned.getId());
        assertEquals("INTERNAL", valueReturned.getType());
        assertEquals(new BigDecimal("900.00"), source.getBalance());
        assertEquals(new BigDecimal("600.00"), destination.getBalance());
        verify(transferRepositoryPort, times(1)).save(any(Transfer.class));
        verify(movementRepositoryPort, times(2)).save(any(Movement.class));
        verify(accountRepositoryPort, times(2)).saveBalance(any(Account.class));
    }

    @Test
    void shouldThrowWhenTransferNotExists() {
        when(transferRepositoryPort.findById(10L, 99L))
                .thenReturn(Optional.empty());

        assertThrows(TransferNotFoundException.class,
                () -> transferService.findById(10L, 99L));
    }

    private MakeTransferCommand command(Long customerId, Long sourceAccountId, String amount, String destinationCci) {
        return new MakeTransferCommand(customerId, new BigDecimal(amount), destinationCci, "BCP", "Luis Ramirez",
                sourceAccountId);
    }

    private Account account(Long id, String cci, String currency, String balance, Long customerId) {
        return new Account(id, "19100000000" + id, cci, "SAVINGS", currency, new BigDecimal(balance), "ACTIVE",
                LocalDateTime.now(), customerId);
    }

    private Transfer withId(Transfer transfer, Long id) {
        return new Transfer(id, transfer.getAmount(), transfer.getCurrency(), transfer.getStatus(),
                transfer.getRejectionReason(), transfer.getRequestedAt(), transfer.getProcessedAt(), transfer.getType(),
                transfer.getDestinationCci(), transfer.getDestinationBank(), transfer.getDestinationHolder(),
                transfer.getSourceAccountId(), transfer.getDestinationAccountId());
    }
}
