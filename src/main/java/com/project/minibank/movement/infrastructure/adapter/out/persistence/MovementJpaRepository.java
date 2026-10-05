package com.project.minibank.movement.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovementJpaRepository extends JpaRepository<MovementEntity, Long> {

    List<MovementEntity> findAllByAccountId(Long accountId);
}
