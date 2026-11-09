package com.project.minibank.customer.domain.model;

import com.project.minibank.customer.domain.exception.CustomerNotActiveException;
import com.project.minibank.customer.domain.exception.FavoriteAliasAlreadyUsedException;
import com.project.minibank.customer.domain.exception.FavoriteNotFoundException;
import com.project.minibank.customer.domain.exception.FavoriteAlreadyExistsException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Customer {

    private final Long id;
    private final String documentType;
    private final String documentNumber;
    private final String firstNames;
    private final String lastNames;
    private final String email;
    private final String phone;
    private final CustomerStatus status;
    private final LocalDateTime registeredAt;
    private final List<Favorite> favorites;

    public Customer(Long id, String documentType, String documentNumber, String firstNames, String lastNames,
                    String email, String phone, CustomerStatus status, LocalDateTime registeredAt,
                    List<Favorite> favorites) {
        if (documentNumber == null || documentNumber.isBlank()) {
            throw new IllegalArgumentException("El número de documento es obligatorio");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        this.id = id;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.firstNames = firstNames;
        this.lastNames = lastNames;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.registeredAt = registeredAt;
        this.favorites = new ArrayList<>(favorites);
    }

    // Alta de un cliente nuevo: siempre empieza activo y sin favoritos.
    public static Customer register(String documentType, String documentNumber, String firstNames, String lastNames,
                                    String email, String phone, LocalDateTime now) {
        return new Customer(null, documentType, documentNumber, firstNames, lastNames, email, phone,
                CustomerStatus.ACTIVE, now, List.of());
    }

    public Favorite addFavorite(String alias, String accountNumber, String bank, String holder) {
        if (!isActive()) {
            throw new CustomerNotActiveException(id);
        }

        Favorite favorite = Favorite.create(alias, accountNumber, bank, holder, id);

        if (favorites.stream().anyMatch(f -> f.isSameAccountAs(favorite))) {
            throw new FavoriteAlreadyExistsException(favorite.getAccountNumber(), favorite.getBank());
        }
        if (favorites.stream().anyMatch(f -> f.hasSameAliasAs(favorite))) {
            throw new FavoriteAliasAlreadyUsedException(favorite.getAlias());
        }

        favorites.add(favorite);
        return favorite;
    }

    // Solo se modifica un favorito propio: si no está en la lista de este cliente, para él no existe (404).
    public Favorite updateFavorite(Long favoriteId, String alias, String accountNumber, String bank, String holder) {
        if (!isActive()) {
            throw new CustomerNotActiveException(id);
        }
        Favorite current = findFavorite(favoriteId);
        Favorite updated = current.withData(alias, accountNumber, bank, holder);
        List<Favorite> others = favorites.stream().filter(f -> !f.getId().equals(favoriteId)).toList();
        if (others.stream().anyMatch(f -> f.isSameAccountAs(updated))) {
            throw new FavoriteAlreadyExistsException(updated.getAccountNumber(), updated.getBank());
        }
        if (others.stream().anyMatch(f -> f.hasSameAliasAs(updated))) {
            throw new FavoriteAliasAlreadyUsedException(updated.getAlias());
        }
        favorites.set(favorites.indexOf(current), updated);
        return updated;
    }

    // Borrar no exige que el cliente esté activo: quitar un dato propio no tiene riesgo.
    public Favorite removeFavorite(Long favoriteId) {
        Favorite favorite = findFavorite(favoriteId);
        favorites.remove(favorite);
        return favorite;
    }

    private Favorite findFavorite(Long favoriteId) {
        return favorites.stream()
                .filter(f -> f.getId().equals(favoriteId))
                .findFirst()
                .orElseThrow(() -> new FavoriteNotFoundException(id, favoriteId));
    }

    public boolean isActive() {
        return status == CustomerStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getFirstNames() {
        return firstNames;
    }

    public String getLastNames() {
        return lastNames;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public List<Favorite> getFavorites() {
        return favorites;
    }
}
