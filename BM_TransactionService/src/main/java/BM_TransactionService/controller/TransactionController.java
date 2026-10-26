package BM_TransactionService.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import BM_TransactionService.dto.TransactionRequest;
import BM_TransactionService.dto.TransactionResponse;
import BM_TransactionService.service.TransactionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

	private final TransactionService transactionService;

	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	// Deposit money
	@PostMapping("/deposit")
	public ResponseEntity<TransactionResponse> deposit(
			@RequestHeader("Idempotency-Key") String idempotencyKey,
			@Valid @RequestBody TransactionRequest request) {

		TransactionResponse response =
				transactionService.deposit(
						request,
						idempotencyKey);

		return new ResponseEntity<>(
				response,
				HttpStatus.CREATED
				);
	}

	// Withdraw money
	@PostMapping("/withdraw")
	public ResponseEntity<TransactionResponse> withdraw(
			@RequestHeader("Idempotency-Key") String idempotencyKey,
			@Valid @RequestBody TransactionRequest request) {

		TransactionResponse response =
				transactionService.withdraw(
						request,
						idempotencyKey);

		return new ResponseEntity<>(
				response,
				HttpStatus.CREATED
				);
	}

	// Transfer money
	@PostMapping("/transfer")
	public ResponseEntity<TransactionResponse> transfer(
			@RequestHeader("Idempotency-Key") String idempotencyKey,
			@Valid @RequestBody TransactionRequest request) {

		TransactionResponse response =
				transactionService.transfer(
						request,
						idempotencyKey);

		return new ResponseEntity<>(
				response,
				HttpStatus.CREATED
				);
	}

	// Get transaction by ID
	@GetMapping("/{transactionId}")
	public ResponseEntity<TransactionResponse> getTransactionById(
			@PathVariable Long transactionId) {

		TransactionResponse response =
				transactionService.getTransactionById(transactionId);

		return ResponseEntity.ok(response);
	}

	// Get transaction history
	@GetMapping("/account/{accountNumber}")
	public ResponseEntity<List<TransactionResponse>> getTransactionsByAccountNumber(
			@PathVariable Long accountNumber) {

		List<TransactionResponse> transactions =
				transactionService.getTransactionsByAccountNumber(
						accountNumber);

		return ResponseEntity.ok(transactions);
	}
}