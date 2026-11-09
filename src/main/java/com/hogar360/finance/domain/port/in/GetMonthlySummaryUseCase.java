package com.hogar360.finance.domain.port.in;

import com.hogar360.finance.application.dto.MonthlySummaryQuery;
import com.hogar360.finance.domain.model.MonthlySummary;

public interface GetMonthlySummaryUseCase {
    MonthlySummary getSummary(MonthlySummaryQuery query);
}