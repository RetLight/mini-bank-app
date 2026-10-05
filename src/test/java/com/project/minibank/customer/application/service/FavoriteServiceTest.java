package com.project.minibank.customer.application.service;

import com.project.minibank.customer.application.port.in.AddFavoriteCommand;
import com.project.minibank.customer.application.port.out.CustomerRepositoryPort;
import com.project.minibank.customer.application.port.out.FavoriteRepositoryPort;
import com.project.minibank.customer.domain.exception.CustomerNotFoundException;
import com.project.minibank.customer.domain.exception.FavoriteAliasAlreadyUsedException;
import com.project.minibank.customer.domain.exception.FavoriteNotFoundException;
import com.project.minibank.customer.domain.model.Customer;
import com.project.minibank.customer.domain.model.CustomerStatus;
import com.project.minibank.customer.domain.model.Favorite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {

    @Mock
    FavoriteRepositoryPort favoriteRepository;

    @Mock
    CustomerRepositoryPort customerRepository;

    @InjectMocks
    FavoriteService favoriteService;

    @Test
    void shouldThrowWhenCustomerNotExists() {
        when(customerRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> favoriteService.addFavorite(new AddFavoriteCommand(99L, "Mama", "19112345678", "BCP", "Maria Perez")));
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void shouldSaveFavoriteOnce() {
        Long customerId = 10L;
        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer(customerId, List.of())));
        when(favoriteRepository.save(any(Favorite.class)))
                .thenReturn(new Favorite(1L, "Mama", "19112345678", "BCP", "Maria Perez", customerId));

        Favorite valueReturned = favoriteService.addFavorite(
                new AddFavoriteCommand(customerId, "Mama", "19112345678", "BCP", "Maria Perez"));

        assertNotNull(valueReturned.getId());
        assertEquals("Mama", valueReturned.getAlias());
        verify(favoriteRepository, times(1)).save(any(Favorite.class));
    }

    @Test
    void shouldNotSaveWhenAliasIsRepeated() {
        Long customerId = 10L;
        Favorite existing = new Favorite(1L, "Mama", "19112345678", "BCP", "Maria Perez", customerId);
        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer(customerId, List.of(existing))));

        assertThrows(FavoriteAliasAlreadyUsedException.class,
                () -> favoriteService.addFavorite(new AddFavoriteCommand(customerId, "MAMA", "19100000000", "BCP", "Otra")));
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void shouldThrowWhenFavoriteNotExists() {
        when(favoriteRepository.findById(10L, 99L))
                .thenReturn(Optional.empty());

        assertThrows(FavoriteNotFoundException.class,
                () -> favoriteService.findById(10L, 99L));
    }

    private Customer customer(Long id, List<Favorite> favorites) {
        return new Customer(id, "DNI", "10000001", "Ana", "Torres", "ana.torres@mail.com", null,
                CustomerStatus.ACTIVE, LocalDateTime.now(), favorites);
    }
}
