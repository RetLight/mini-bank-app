package com.project.minibank.customer.application.service;

import com.project.minibank.customer.application.port.in.AddFavoriteCommand;
import com.project.minibank.customer.application.port.in.AddFavoriteUseCase;
import com.project.minibank.customer.application.port.in.DeleteFavoriteUseCase;
import com.project.minibank.customer.application.port.in.GetFavoriteUseCase;
import com.project.minibank.customer.application.port.in.UpdateFavoriteCommand;
import com.project.minibank.customer.application.port.in.UpdateFavoriteUseCase;
import com.project.minibank.customer.application.port.out.CustomerRepositoryPort;
import com.project.minibank.customer.application.port.out.FavoriteRepositoryPort;
import com.project.minibank.customer.application.port.out.UserStatus;
import com.project.minibank.customer.application.port.out.UserStatusPort;
import com.project.minibank.customer.domain.exception.CustomerNotFoundException;
import com.project.minibank.customer.domain.exception.FavoriteNotFoundByAliasException;
import com.project.minibank.customer.domain.exception.FavoriteNotFoundException;
import com.project.minibank.customer.domain.exception.UserBlockedException;
import com.project.minibank.customer.domain.exception.UserNoLongerExistsException;
import com.project.minibank.customer.domain.model.Customer;
import com.project.minibank.customer.domain.model.Favorite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteService implements AddFavoriteUseCase, GetFavoriteUseCase, UpdateFavoriteUseCase,
        DeleteFavoriteUseCase {

    private static final Logger log = LoggerFactory.getLogger(FavoriteService.class);

    private final FavoriteRepositoryPort favoriteRepository;
    private final CustomerRepositoryPort customerRepository;

    private final UserStatusPort userStatus;

    public FavoriteService(FavoriteRepositoryPort favoriteRepository, CustomerRepositoryPort customerRepository,
                           UserStatusPort userStatus) {
        this.favoriteRepository = favoriteRepository;
        this.customerRepository = customerRepository;
        this.userStatus = userStatus;
    }

    @Override
    public Favorite addFavorite(AddFavoriteCommand command) {
        Customer customer = customerRepository.findById(command.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(command.customerId()));

        // Primero las reglas locales: si fallan, no hace falta preguntar a nadie.
        Favorite favorite = customer.addFavorite(command.alias(), command.accountNumber(), command.bank(), command.holder());

        // Después, auth-service: el token prueba quién era el usuario al emitirse; esto pregunta si sigue activo.
        // Decisión de negocio: si auth-service no puede responder, el favorito se ACEPTA (el token firmado ya prueba
        // la identidad) marcado como no verificado. Rechazar todas las altas mientras auth está caído sería peor.
        // Este método no es @Transactional a propósito: no se retiene una conexión a la base durante la llamada.
        Favorite toSave = switch (userStatus.statusOf(command.customerId())) {
            case UserStatus.Active active -> favorite;
            case UserStatus.Blocked blocked -> throw new UserBlockedException(command.customerId());
            case UserStatus.NotFound notFound -> throw new UserNoLongerExistsException(command.customerId());
            case UserStatus.Unavailable unavailable -> {
                log.warn("Usuario del cliente {} sin verificar ({}): favorito aceptado igualmente", command.customerId(),
                        unavailable.reason());
                yield favorite.unverified();
            }
        };
        return favoriteRepository.save(toSave);
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

    @Override
    @Transactional
    public Favorite updateFavorite(UpdateFavoriteCommand command) {
        Customer customer = findCustomer(command.customerId());
        Favorite updated = customer.updateFavorite(command.favoriteId(), command.alias(), command.accountNumber(),
                command.bank(), command.holder());
        return favoriteRepository.save(updated);
    }

    @Override
    @Transactional
    public void deleteFavorite(Long customerId, Long favoriteId) {
        Customer customer = findCustomer(customerId);
        favoriteRepository.delete(customer.removeFavorite(favoriteId));
    }

    private Customer findCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }
}
