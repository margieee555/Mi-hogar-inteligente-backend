package com.hogar360.finance.infrastructure.adapter.in.web.mapper;

import com.hogar360.finance.application.dto.CreateTransactionCommand;
import com.hogar360.finance.application.dto.ListTransactionsQuery;
import com.hogar360.finance.application.dto.MonthlySummaryQuery;
import com.hogar360.finance.application.dto.UpdateTransactionCommand;
import com.hogar360.finance.domain.model.TransactionType;
import com.hogar360.finance.infrastructure.adapter.in.web.dto.TransactionRequest;
import com.hogar360.shared.exception.DomainValidationException;

import org.springframework.stereotype.Component;

import java.time.YearMonth;

@Component
public class FinanceWebMapper {

    public CreateTransactionCommand toCreateCommand(String ownerEmail, TransactionRequest request) {
        return new CreateTransactionCommand(ownerEmail, request.type(), request.category(),
                request.amount(), request.description(), request.date());
    }

    public UpdateTransactionCommand toUpdateCommand(Long transactionId, String ownerEmail, TransactionRequest request) {
        return new UpdateTransactionCommand(transactionId, ownerEmail, request.type(), request.category(),
                request.amount(), request.description(), request.date());
    }

    public ListTransactionsQuery toListQuery(String ownerEmail, Integer year, Integer month, TransactionType type) {
        return new ListTransactionsQuery(ownerEmail, resolvePeriod(year, month), type);
    }

    public MonthlySummaryQuery toSummaryQuery(String ownerEmail, Integer year, Integer month) {
        return new MonthlySummaryQuery(ownerEmail, resolvePeriod(year, month));
    }

    /** Si no llegan año/mes se usa el actual. Valores fuera de rango -> IllegalArgumentException (400). */
    private YearMonth resolvePeriod(Integer year, Integer month) {
        YearMonth now = YearMonth.now();
        int resolvedYear = year != null ? year : now.getYear();
        int resolvedMonth = month != null ? month : now.getMonthValue();

        if (resolvedMonth < 1 || resolvedMonth > 12) {
             throw new DomainValidationException("error.finance.month.invalid", "El mes debe estar entre 1 y 12");
        }
        if (resolvedYear < 2000 || resolvedYear > 2100) {
            throw new DomainValidationException("error.finance.year.invalid", "El año no es válido");
        }
        return YearMonth.of(resolvedYear, resolvedMonth);
    }
    
}