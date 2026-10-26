package BM_AccountService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import BM_AccountService.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(Long accountNumber);

    Optional<Account> findByUserId(Long userId);

    boolean existsByAccountNumber(Long accountNumber);

    boolean existsByUserId(Long userId);

}