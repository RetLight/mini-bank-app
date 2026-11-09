package com.project.minibank.customer.application.service;

import com.project.minibank.customer.application.port.in.AddFavoriteCommand;
import com.project.minibank.customer.application.port.in.UpdateFavoriteCommand;
import com.project.minibank.customer.application.port.out.CustomerRepositoryPort;
import com.project.minibank.customer.application.port.out.FavoriteRepositoryPort;
import com.project.minibank.customer.application.port.out.UserStatus;
import com.project.minibank.customer.application.port.out.UserStatusPort;
import com.project.minibank.customer.domain.exception.CustomerNotFoundException;
import com.project.minibank.customer.domain.exception.FavoriteAliasAlreadyUsedException;
import com.project.minibank.customer.domain.exception.FavoriteNotFoundException;
import com.project.minibank.customer.domain.exception.UserBlockedException;
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

    @Mock
    UserStatusPort userStatusPort;

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
        when(userStatusPort.statusOf(customerId)).thenReturn(new UserStatus.Active());

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

    @Test
    void shouldUpdateOnlyAFavoriteOfTheSameCustomer() {
        Long customerId = 10L;
        Favorite existing = new Favorite(1L, "Mama", "19112345678", "BCP", "Maria Perez", customerId);
        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer(customerId, List.of(existing))));
        when(favoriteRepository.save(any(Favorite.class))).thenAnswer(inv -> inv.getArgument(0));

        Favorite updated = favoriteService.updateFavorite(
                new UpdateFavoriteCommand(customerId, 1L, "Mamá", "19112345678", "BCP", "Maria Perez"));

        assertEquals(1L, updated.getId());
        assertEquals("Mamá", updated.getAlias());
        assertThrows(FavoriteNotFoundException.class, () -> favoriteService.updateFavorite(
                new UpdateFavoriteCommand(customerId, 99L, "Otro", "19100000000", "BCP", "Otro")));
    }

    @Test
    void shouldNotUpdateToAnAliasAlreadyUsedByAnotherFavorite() {
        Long customerId = 10L;
        Favorite mama = new Favorite(1L, "Mama", "19112345678", "BCP", "Maria Perez", customerId);
        Favorite papa = new Favorite(2L, "Papa", "19100000000", "BCP", "Jose Perez", customerId);
        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer(customerId, List.of(mama, papa))));

        assertThrows(FavoriteAliasAlreadyUsedException.class, () -> favoriteService.updateFavorite(
                new UpdateFavoriteCommand(customerId, 2L, "MAMA", "19100000000", "BCP", "Jose Perez")));
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void shouldDeleteOnlyAFavoriteOfTheSameCustomer() {
        Long customerId = 10L;
        Favorite existing = new Favorite(1L, "Mama", "19112345678", "BCP", "Maria Perez", customerId);
        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer(customerId, List.of(existing))));

        favoriteService.deleteFavorite(customerId, 1L);

        verify(favoriteRepository).delete(existing);
        assertThrows(FavoriteNotFoundException.class, () -> favoriteService.deleteFavorite(customerId, 99L));
    }

    private Customer customer(Long id, List<Favorite> favorites) {
        return new Customer(id, "DNI", "10000001", "Ana", "Torres", "ana.torres@mail.com", null,
                CustomerStatus.ACTIVE, LocalDateTime.now(), favorites);
    }

    @Test
    void shouldRejectTheFavoriteWhenTheUserIsBlocked() {
        Long customerId = 10L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer(customerId, List.of())));
        when(userStatusPort.statusOf(customerId)).thenReturn(new UserStatus.Blocked());

        assertThrows(UserBlockedException.class,
                () -> favoriteService.addFavorite(new AddFavoriteCommand(customerId, "Mama", "19112345678", "BCP", "Maria Perez")));
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void shouldAcceptTheFavoriteUnverifiedWhenAuthCannotAnswer() {
        Long customerId = 10L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer(customerId, List.of())));
        when(userStatusPort.statusOf(customerId)).thenReturn(new UserStatus.Unavailable("AuthUnavailableException"));
        when(favoriteRepository.save(any(Favorite.class))).thenAnswer(inv -> inv.getArgument(0));

        Favorite saved = favoriteService.addFavorite(new AddFavoriteCommand(customerId, "Mama", "19112345678", "BCP", "Maria Perez"));

        assertFalse(saved.isOwnerVerified());
    }

    @Test
    void shouldNotAskAuthWhenALocalRuleFails() {
        Long customerId = 10L;
        Favorite existing = new Favorite(1L, "Mama", "19112345678", "BCP", "Maria Perez", customerId);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer(customerId, List.of(existing))));

        assertThrows(FavoriteAliasAlreadyUsedException.class,
                () -> favoriteService.addFavorite(new AddFavoriteCommand(customerId, "mama", "19100000000", "BCP", "Otra")));
        verify(userStatusPort, never()).statusOf(any());
    }
}
