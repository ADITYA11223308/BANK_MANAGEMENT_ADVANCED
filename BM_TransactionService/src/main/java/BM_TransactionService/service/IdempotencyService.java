package BM_TransactionService.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import BM_TransactionService.entity.IdempotencyEntity;
import BM_TransactionService.repository.IdempotencyRepository;

@Service
public class IdempotencyService {

    private final IdempotencyRepository idempotencyRepository;

    public IdempotencyService(
            IdempotencyRepository idempotencyRepository) {

        this.idempotencyRepository = idempotencyRepository;
    }

    public Optional<IdempotencyEntity> findByKey(
            String idempotencyKey) {

        return idempotencyRepository
                .findByIdempotencyKey(idempotencyKey);
    }

    public IdempotencyEntity save(
            String idempotencyKey,
            String operation,
            Long transactionId,
            String status) {

        IdempotencyEntity entity = new IdempotencyEntity();

        entity.setIdempotencyKey(idempotencyKey);
        entity.setOperation(operation);
        entity.setTransactionId(transactionId);
        entity.setStatus(status);
        entity.setCreatedAt(LocalDateTime.now());

        return idempotencyRepository.save(entity);
    }
}