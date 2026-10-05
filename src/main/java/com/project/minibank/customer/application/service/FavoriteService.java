package com.project.minibank.customer.application.service;

import com.project.minibank.customer.application.port.in.AddFavoriteCommand;
import com.project.minibank.customer.application.port.in.AddFavoriteUseCase;
import com.project.minibank.customer.application.port.in.GetFavoriteUseCase;
import com.project.minibank.customer.application.port.out.CustomerRepositoryPort;
import com.project.minibank.customer.application.port.out.FavoriteRepositoryPort;
import com.project.minibank.customer.domain.exception.CustomerNotFoundException;
import com.project.minibank.customer.domain.exception.FavoriteNotFoundByAliasException;
import com.project.minibank.customer.domain.exception.FavoriteNotFoundException;
import com.project.minibank.customer.domain.model.Customer;
import com.project.minibank.customer.domain.model.Favorite;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService implements AddFavoriteUseCase, GetFavoriteUseCase {

    private final FavoriteRepositoryPort favoriteRepository;
    private final CustomerRepositoryPort customerRepository;

    public FavoriteService(FavoriteRepositoryPort favoriteRepository, CustomerRepositoryPort customerRepository) {
        this.favoriteRepository = favoriteRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Favorite addFavorite(AddFavoriteCommand command) {
        Customer customer = customerRepository.findById(command.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(command.customerId()));

        Favorite favorite = customer.addFavorite(command.alias(), command.accountNumber(), command.bank(), command.holder());
        return favoriteRepository.save(favorite);
    }

    @Override
    public Favorite findById(Long customerId, Long favoriteId) {
        return favoriteRepository.findById(customerId, favoriteId)
                .orElseThrow(() -> new FavoriteNotFoundException(customerId, favoriteId));
    }

    @Override
    public Favorite findByAlias(Long customerId, String alias) {
        return favoriteRepository.findByAlias(customerId, alias)
                .orElseThrow(() -> new FavoriteNotFoundByAliasException(customerId, alias));
    }

    @Override
    public List<Favorite> findAll(Long customerId) {
        return favoriteRepository.findAll(customerId);
    }
}
