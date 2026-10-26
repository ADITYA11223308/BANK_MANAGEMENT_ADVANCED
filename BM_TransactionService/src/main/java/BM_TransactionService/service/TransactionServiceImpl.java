package BM_TransactionService.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import BM_TransactionService.dto.AccountResponse;
import BM_TransactionService.dto.TransactionRequest;
import BM_TransactionService.dto.TransactionResponse;
import BM_TransactionService.entity.BalanceOperation;
import BM_TransactionService.entity.Transaction;
import BM_TransactionService.entity.TransactionStatus;
import BM_TransactionService.entity.TransactionType;
import BM_TransactionService.event.TransactionCompletedEvent;
import BM_TransactionService.event.TransactionEventProducer;
import BM_TransactionService.exception.TransactionException;
import BM_TransactionService.repository.TransactionRepository;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final RestClient restClient;
    private final TransactionEventProducer transactionEventProducer;
    private final HttpServletRequest httpServletRequest;
    private final IdempotencyService idempotencyService;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            RestClient restClient,
            TransactionEventProducer transactionEventProducer,
            HttpServletRequest httpServletRequest,
            IdempotencyService idempotencyService) {

        this.transactionRepository = transactionRepository;
        this.restClient = restClient;
        this.transactionEventProducer = transactionEventProducer;
        this.httpServletRequest = httpServletRequest;
        this.idempotencyService = idempotencyService;
    }

    @Override
    public TransactionResponse transfer(
            TransactionRequest request,
            String idempotencyKey) {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new TransactionException(
                    "Idempotency key is required");
        }

        // Keep your existing idempotency validation and lookup here.

        if (request.getAmount() == null
                || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionException(
                    "Transfer amount must be greater than zero");
        }

        if (request.getAccountNumber() == null
                || request.getReceiverAccountNumber() == null) {
            throw new TransactionException(
                    "Sender and receiver account numbers are required");
        }

        if (request.getAccountNumber()
                .equals(request.getReceiverAccountNumber())) {
            throw new TransactionException(
                    "Sender and receiver accounts must be different");
        }

        // Sender lookup remains unchanged.
        AccountResponse sender =
                getAccount(request.getAccountNumber());

        if (sender.getBalance().compareTo(request.getAmount()) < 0) {
            throw new TransactionException(
                    "Insufficient balance");
        }

        // Sender withdrawal remains unchanged.
        AccountResponse updatedSender = updateBalance(
                sender.getAccountNumber(),
                request.getAmount(),
                BalanceOperation.WITHDRAW);

        boolean receiverDepositCompleted = false;

        try {
            // Deposit into the receiver without the ordinary receiver lookup.
            restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/{receiverAccountNumber}/transfer-deposit")
                            .queryParam(
                                    "senderAccountNumber",
                                    sender.getAccountNumber())
                            .queryParam("amount", request.getAmount())
                            .build(request.getReceiverAccountNumber()))
                    .header(
                            "Authorization",
                            httpServletRequest.getHeader("Authorization"))
                    .retrieve()
                    .body(AccountResponse.class);

            receiverDepositCompleted = true;

            // Save transaction using your existing entity.
            Transaction transaction = new Transaction();
            transaction.setAccountNumber(
                    request.getAccountNumber());
            transaction.setReceiverAccountNumber(
                    request.getReceiverAccountNumber());
            transaction.setAmount(request.getAmount());
            transaction.setTransactionType(TransactionType.TRANSFER);
            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setTransactionTime(LocalDateTime.now());
            transaction.setBalanceAfterTransaction(
                    updatedSender.getBalance());

            Transaction savedTransaction =
                    transactionRepository.save(transaction);

            // Publish the event using your existing event class and producer.
            TransactionCompletedEvent event =
                    new TransactionCompletedEvent(
                            savedTransaction.getTransactionId(),
                            savedTransaction.getAccountNumber(),
                            savedTransaction.getAmount(),
                            savedTransaction.getTransactionType().name(),
                            savedTransaction.getStatus().name(),
                            savedTransaction.getReceiverAccountNumber(),
                            savedTransaction.getTransactionTime(),
                            savedTransaction.getBalanceAfterTransaction());

            transactionEventProducer.publishTransactionCompleted(event);

            // Keep your existing idempotency result-saving logic here.

            return mapToResponse(savedTransaction);

        } catch (Exception exception) {

            // Compensate the receiver deposit if it completed.
            if (receiverDepositCompleted) {
                try {
                    updateBalance(
                            request.getReceiverAccountNumber(),
                            request.getAmount(),
                            BalanceOperation.WITHDRAW);
                } catch (Exception compensationException) {
                    exception.addSuppressed(compensationException);
                }
            }

            // Refund the sender if transfer processing fails.
            try {
                updateBalance(
                        sender.getAccountNumber(),
                        request.getAmount(),
                        BalanceOperation.DEPOSIT);
            } catch (Exception refundException) {
                exception.addSuppressed(refundException);
            }

            throw new TransactionException("Transfer failed");
        }
    }

    private AccountResponse getAccount(Long accountNumber) {
        int attempts = 0;

        while (true) {
            try {
                return restClient.get()
                        .uri("/accounts/{accountNumber}", accountNumber)
                        .header(
                                "Authorization",
                                httpServletRequest.getHeader("Authorization"))
                        .retrieve()
                        .body(AccountResponse.class);

            } catch (ResourceAccessException exception) {
                attempts++;

                if (attempts >= 3) {
                    throw exception;
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new TransactionException(
                            "Account request interrupted");
                }
            }
        }
    }

    private AccountResponse updateBalance(
            Long accountNumber,
            BigDecimal amount,
            BalanceOperation operation) {

        return restClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/{accountNumber}/balance")
                        .queryParam("amount", amount)
                        .queryParam("operation", operation)
                        .build(accountNumber))
                .header(
                        "Authorization",
                        httpServletRequest.getHeader("Authorization"))
                .retrieve()
                .body(AccountResponse.class);
    }

    @Override
    public TransactionResponse deposit(
            TransactionRequest request,
            String idempotencyKey) {
        // Keep your existing implementation here.
        throw new UnsupportedOperationException(
                "Deposit implementation is not included in the supplied code");
    }

    @Override
    public TransactionResponse withdraw(
            TransactionRequest request,
            String idempotencyKey) {
        // Keep your existing implementation here.
        throw new UnsupportedOperationException(
                "Withdraw implementation is not included in the supplied code");
    }

    @Override
    public TransactionResponse getTransactionById(Long transactionId) {
        // Keep your existing implementation here.
        throw new UnsupportedOperationException(
                "Transaction lookup implementation is not included in the supplied code");
    }

    @Override
    public List<TransactionResponse> getTransactionsByAccountNumber(
            Long accountNumber) {
        // Keep your existing implementation here.
        throw new UnsupportedOperationException(
                "Account transaction lookup implementation is not included in the supplied code");
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        // Use the mapping from your existing implementation.
        throw new UnsupportedOperationException(
                "Use your existing TransactionResponse mapping");
    }
}