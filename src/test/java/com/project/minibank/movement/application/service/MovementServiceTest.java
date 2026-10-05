package com.project.minibank.movement.application.service;

import com.project.minibank.movement.application.port.out.MovementRepositoryPort;
import com.project.minibank.movement.domain.exception.MovementNotFoundException;
import com.project.minibank.movement.domain.model.Movement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovementServiceTest {

    @Mock
    MovementRepositoryPort movementRepositoryPort;

    @InjectMocks
    MovementService movementService;

    @Test
    void shouldThrowWhenMovementNotExists() {
        when(movementRepositoryPort.findById(10L, 99L))
                .thenReturn(Optional.empty());

        assertThrows(MovementNotFoundException.class,
                () -> movementService.findById(10L, 99L));
    }

    @Test
    void shouldReturnMovementIfExists() {
        Movement movement = new Movement(5L, "DECREASE", new BigDecimal("100.00"), new BigDecimal("900.00"),
                LocalDateTime.now(), 1L, 1L);
        when(movementRepositoryPort.findById(10L, 5L))
                .thenReturn(Optional.of(movement));

        Movement valueReturned = movementService.findById(10L, 5L);

        assertEquals(5L, valueReturned.getId());
        assertEquals("DECREASE", valueReturned.getType());
    }

    @Test
    void shouldReturnMovementsOfAccount() {
        when(movementRepositoryPort.findAllByAccount(10L, 1L))
                .thenReturn(List.of(new Movement(5L, "DECREASE", new BigDecimal("100.00"), new BigDecimal("900.00"),
                        LocalDateTime.now(), 1L, 1L)));

        List<Movement> valueReturned = movementService.findAllByAccount(10L, 1L);

        assertEquals(1, valueReturned.size());
        verify(movementRepositoryPort).findAllByAccount(eq(10L), eq(1L));
    }
}
