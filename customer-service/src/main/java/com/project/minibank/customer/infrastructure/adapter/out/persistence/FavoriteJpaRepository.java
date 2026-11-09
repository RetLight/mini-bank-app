package com.project.minibank.customer.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteJpaRepository extends JpaRepository<FavoriteEntity, Long> {

    Optional<FavoriteEntity> findByIdAndCustomerId(Long id, Long customerId);

    Optional<FavoriteEntity> findByCustomerIdAndAliasIgnoreCase(Long customerId, String alias);

    List<FavoriteEntity> findAllByCustomerId(Long customerId);
}
