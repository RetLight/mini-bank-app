package com.project.minibank.movement.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByIdAndCustomerId(Long id, Long customerId);

    Optional<AccountEntity> findByCci(String cci);

    List<AccountEntity> findAllByCustomerId(Long customerId);
}
