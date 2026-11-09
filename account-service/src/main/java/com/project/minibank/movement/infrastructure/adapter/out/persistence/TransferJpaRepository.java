package com.project.minibank.movement.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferJpaRepository extends JpaRepository<TransferEntity, Long> {

    List<TransferEntity> findAllBySourceAccountId(Long accountId);
}
