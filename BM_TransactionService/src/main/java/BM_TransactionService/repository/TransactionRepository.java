package BM_TransactionService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import BM_TransactionService.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountNumberOrderByTransactionTimeDesc(
            Long accountNumber);

    List<Transaction> findByReceiverAccountNumberOrderByTransactionTimeDesc(
            Long receiverAccountNumber);
}