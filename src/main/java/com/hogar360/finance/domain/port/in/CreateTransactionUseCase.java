package com.hogar360.finance.domain.port.in;

import com.hogar360.finance.application.dto.CreateTransactionCommand;
import com.hogar360.finance.domain.model.Transaction;

public interface CreateTransactionUseCase {
    Transaction create(CreateTransactionCommand command);
}