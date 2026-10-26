package BM_TransactionService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import BM_TransactionService.entity.IdempotencyEntity;

@Repository
public interface IdempotencyRepository
        extends JpaRepository<IdempotencyEntity, Long> {

    Optional<IdempotencyEntity> findByIdempotencyKey(
            String idempotencyKey);
}