package com.project.minibank.customer.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long> {

    @EntityGraph(attributePaths = "favorites")
    Optional<CustomerEntity> findWithFavoritesById(Long id);

    // Con los favoritos en la misma consulta: sin open-in-view, cargarlos después fallaría fuera de la transacción.
    @EntityGraph(attributePaths = "favorites")
    List<CustomerEntity> findAllByOrderByIdAsc();

    boolean existsByDocumentNumberOrEmailIgnoreCase(String documentNumber, String email);
}
