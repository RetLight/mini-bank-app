package com.project.minibank.auth.infrastructure.adapter.out.persistence;

import com.project.minibank.auth.domain.model.User;

final class UserPersistenceMapper {

    private UserPersistenceMapper() {
    }

    static User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getPasswordHash(),
                entity.getRole(),
                entity.isBlocked(),
                entity.getCustomerId()
        );
    }
}
