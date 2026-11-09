package com.hogar360.finance.infrastructure.adapter.in.web;

import com.hogar360.finance.domain.model.MonthlySummary;
import com.hogar360.finance.domain.model.Transaction;
import com.hogar360.finance.domain.model.TransactionCategory;
import com.hogar360.finance.domain.model.TransactionType;
import com.hogar360.finance.domain.port.in.CreateTransactionUseCase;
import com.hogar360.finance.domain.port.in.DeleteTransactionUseCase;
import com.hogar360.finance.domain.port.in.GetMonthlySummaryUseCase;
import com.hogar360.finance.domain.port.in.ListTransactionsUseCase;
import com.hogar360.finance.domain.port.in.UpdateTransactionUseCase;
import com.hogar360.finance.infrastructure.adapter.in.web.dto.CategoryResponse;
import com.hogar360.finance.infrastructure.adapter.in.web.dto.MonthlySummaryResponse;
import com.hogar360.finance.infrastructure.adapter.in.web.dto.TransactionRequest;
import com.hogar360.finance.infrastructure.adapter.in.web.dto.TransactionResponse;
import com.hogar360.finance.infrastructure.adapter.in.web.mapper.FinanceWebMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * Adaptador de entrada HTTP de finanzas. Protegido por el JwtAuthenticationFilter
 * (no está en permitAll): authentication.getName() es el email del JWT.
 */
@RestController
@RequestMapping("/api/v1/finance")
public class FinanceController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final UpdateTransactionUseCase updateTransactionUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final GetMonthlySummaryUseCase getMonthlySummaryUseCase;
    private final FinanceWebMapper mapper;

    public FinanceController(CreateTransactionUseCase createTransactionUseCase,
                             UpdateTransactionUseCase updateTransactionUseCase,
                             DeleteTransactionUseCase deleteTransactionUseCase,
                             ListTransactionsUseCase listTransactionsUseCase,
                             GetMonthlySummaryUseCase getMonthlySummaryUseCase,
                             FinanceWebMapper mapper) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.updateTransactionUseCase = updateTransactionUseCase;
        this.deleteTransactionUseCase = deleteTransactionUseCase;
        this.listTransactionsUseCase = listTransactionsUseCase;
        this.getMonthlySummaryUseCase = getMonthlySummaryUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/transactions")
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request,
                                                      Authentication authentication) {
        Transaction transaction = createTransactionUseCase.create(
                mapper.toCreateCommand(authentication.getName(), request));
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.from(transaction));
    }

    @PutMapping("/transactions/{id}")
    public ResponseEntity<TransactionResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody TransactionRequest request,
                                                      Authentication authentication) {
        Transaction transaction = updateTransactionUseCase.update(
                mapper.toUpdateCommand(id, authentication.getName(), request));
        return ResponseEntity.ok(TransactionResponse.from(transaction));
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        deleteTransactionUseCase.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> list(@RequestParam(required = false) Integer year,
                                                          @RequestParam(required = false) Integer month,
                                                          @RequestParam(required = false) TransactionType type,
                                                          Authentication authentication) {
        List<TransactionResponse> body = listTransactionsUseCase
                .list(mapper.toListQuery(authentication.getName(), year, month, type))
                .stream()
                .map(TransactionResponse::from)
                .toList();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/summary")
    public ResponseEntity<MonthlySummaryResponse> summary(@RequestParam(required = false) Integer year,
                                                          @RequestParam(required = false) Integer month,
                                                          Authentication authentication) {
        MonthlySummary summary = getMonthlySummaryUseCase.getSummary(
                mapper.toSummaryQuery(authentication.getName(), year, month));
        return ResponseEntity.ok(MonthlySummaryResponse.from(summary));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> categories() {
        List<CategoryResponse> categories = Arrays.stream(TransactionCategory.values())
                .map(CategoryResponse::from)
                .toList();
        return ResponseEntity.ok(categories);
    }
}