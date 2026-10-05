package com.project.minibank.customer.infrastructure.adapter.out.persistence;

import com.project.minibank.customer.application.port.out.FavoriteRepositoryPort;
import com.project.minibank.customer.domain.model.Favorite;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FavoritePersistenceAdapter implements FavoriteRepositoryPort {

    private final FavoriteJpaRepository favoriteJpaRepository;
    private final CustomerJpaRepository customerJpaRepository;

    public FavoritePersistenceAdapter(FavoriteJpaRepository favoriteJpaRepository,
                                      CustomerJpaRepository customerJpaRepository) {
        this.favoriteJpaRepository = favoriteJpaRepository;
        this.customerJpaRepository = customerJpaRepository;
    }

    @Override
    public Favorite save(Favorite favorite) {
        CustomerEntity customer = customerJpaRepository.getReferenceById(favorite.getCustomerId());

        FavoriteEntity saved = favoriteJpaRepository.save(FavoritePersistenceMapper.toEntity(favorite, customer));
        return FavoritePersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Favorite> findById(Long customerId, Long favoriteId) {
        return favoriteJpaRepository.findByIdAndCustomerId(favoriteId, customerId).map(FavoritePersistenceMapper::toDomain);
    }

    @Override
    public List<Favorite> findAll(Long customerId) {
        return toDomain(favoriteJpaRepository.findAllByCustomerId(customerId));
    }

    @Override
    public Optional<Favorite> findByAlias(Long customerId, String alias) {
        return favoriteJpaRepository.findByCustomerIdAndAliasIgnoreCase(customerId, alias).map(FavoritePersistenceMapper::toDomain);
    }

    private List<Favorite> toDomain(List<FavoriteEntity> entities) {
        return entities.stream()
                .map(FavoritePersistenceMapper::toDomain)
                .toList();
    }
}
