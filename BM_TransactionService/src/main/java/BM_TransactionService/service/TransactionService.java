package BM_TransactionService.service;

import java.util.List;

import BM_TransactionService.dto.TransactionRequest;
import BM_TransactionService.dto.TransactionResponse;

public interface TransactionService {

    TransactionResponse deposit(
            TransactionRequest request,
            String idempotencyKey);

    TransactionResponse withdraw(
            TransactionRequest request,
            String idempotencyKey);

    TransactionResponse transfer(
            TransactionRequest request,
            String idempotencyKey);

    TransactionResponse getTransactionById(Long transactionId);

    List<TransactionResponse> getTransactionsByAccountNumber(
            Long accountNumber);
}