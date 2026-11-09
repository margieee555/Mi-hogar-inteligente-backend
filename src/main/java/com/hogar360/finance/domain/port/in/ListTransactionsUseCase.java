package com.hogar360.finance.domain.port.in;

import com.hogar360.finance.application.dto.ListTransactionsQuery;
import com.hogar360.finance.domain.model.Transaction;

import java.util.List;

public interface ListTransactionsUseCase {
    List<Transaction> list(ListTransactionsQuery query);
}