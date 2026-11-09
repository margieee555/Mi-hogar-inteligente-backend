package com.hogar360.finance.application.service;

import com.hogar360.finance.application.dto.CreateTransactionCommand;
import com.hogar360.finance.application.dto.ListTransactionsQuery;
import com.hogar360.finance.application.dto.MonthlySummaryQuery;
import com.hogar360.finance.application.dto.UpdateTransactionCommand;
import com.hogar360.finance.domain.exception.HouseholdRequiredException;
import com.hogar360.finance.domain.exception.TransactionNotFoundException;
import com.hogar360.finance.domain.model.MonthlySummary;
import com.hogar360.finance.domain.model.Transaction;
import com.hogar360.finance.domain.model.TransactionCategory;
import com.hogar360.finance.domain.model.TransactionType;
import com.hogar360.finance.domain.port.out.TransactionRepositoryPort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FinanceUseCasesTest {

    private static final String EMAIL = "ana@x.com";
    private static final LocalDate DATE = LocalDate.of(2026, 10, 1);

    private final TransactionRepositoryPort repository = mock(TransactionRepositoryPort.class);
    private final FinanceHouseholdResolver resolver = mock(FinanceHouseholdResolver.class);

    private Transaction existing() {
        return new Transaction(7L, 5L, TransactionType.EXPENSE, TransactionCategory.FOOD,
                new BigDecimal("50000.00"), "Mercado", DATE, LocalDateTime.now());
    }

    // ---------- CreateTransactionService ----------

    @Test
    void create_guardaElMovimientoEnElHogarDelUsuario() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        new CreateTransactionService(repository, resolver).create(new CreateTransactionCommand(
                EMAIL, TransactionType.EXPENSE, TransactionCategory.FOOD, new BigDecimal("12000.5"), "Pan", DATE));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(repository).save(captor.capture());
        Transaction saved = captor.getValue();
        assertThat(saved.getId()).isNull();
        assertThat(saved.getHouseholdId()).isEqualTo(5L);
        assertThat(saved.getAmount()).isEqualByComparingTo("12000.50");
        assertThat(saved.getDescription()).isEqualTo("Pan");
    }

    @Test
    void create_noGuardaSiElUsuarioNoTieneHogar() {
        when(resolver.requireHouseholdId(EMAIL)).thenThrow(new HouseholdRequiredException());

        assertThatThrownBy(() -> new CreateTransactionService(repository, resolver).create(
                new CreateTransactionCommand(EMAIL, TransactionType.EXPENSE, TransactionCategory.FOOD,
                        BigDecimal.TEN, null, DATE)))
                .isInstanceOf(HouseholdRequiredException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void create_noGuardaSiLaCategoriaNoCorrespondeAlTipo() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);

        assertThatThrownBy(() -> new CreateTransactionService(repository, resolver).create(
                new CreateTransactionCommand(EMAIL, TransactionType.INCOME, TransactionCategory.FOOD,
                        BigDecimal.TEN, null, DATE)))
                .isInstanceOf(IllegalArgumentException.class);

        verify(repository, never()).save(any());
    }

    // ---------- UpdateTransactionService ----------

    @Test
    void update_actualizaElMovimientoDelHogar() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(7L, 5L)).thenReturn(Optional.of(existing()));
        when(repository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction result = new UpdateTransactionService(repository, resolver).update(new UpdateTransactionCommand(
                7L, EMAIL, TransactionType.EXPENSE, TransactionCategory.TRANSPORT,
                new BigDecimal("8000"), "Bus", DATE));

        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getCategory()).isEqualTo(TransactionCategory.TRANSPORT);
        assertThat(result.getAmount()).isEqualByComparingTo("8000.00");
        assertThat(result.getDescription()).isEqualTo("Bus");
    }

    @Test
    void update_lanzaNotFoundSiElMovimientoNoEsDelHogar() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(7L, 5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new UpdateTransactionService(repository, resolver).update(
                new UpdateTransactionCommand(7L, EMAIL, TransactionType.EXPENSE, TransactionCategory.FOOD,
                        BigDecimal.TEN, null, DATE)))
                .isInstanceOf(TransactionNotFoundException.class);

        verify(repository, never()).save(any());
    }

    // ---------- DeleteTransactionService ----------

    @Test
    void delete_borraElMovimientoDelHogar() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(7L, 5L)).thenReturn(Optional.of(existing()));

        new DeleteTransactionService(repository, resolver).delete(7L, EMAIL);

        verify(repository).deleteById(7L);
    }

    @Test
    void delete_noBorraSiElMovimientoNoEsDelHogar() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(7L, 5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new DeleteTransactionService(repository, resolver).delete(7L, EMAIL))
                .isInstanceOf(TransactionNotFoundException.class);

        verify(repository, never()).deleteById(any());
    }

    // ---------- ListTransactionsService ----------

    @Test
    void list_consultaElMesCompletoSinFiltrarPorTipo() {
        List<Transaction> expected = List.of(existing());
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.search(5L, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28), null)).thenReturn(expected);

        List<Transaction> result = new ListTransactionsService(repository, resolver)
                .list(new ListTransactionsQuery(EMAIL, YearMonth.of(2026, 2), null));

        assertThat(result).isSameAs(expected);
    }

    @Test
    void list_filtraPorTipoCuandoSeIndica() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.search(5L, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), TransactionType.INCOME))
                .thenReturn(List.of());

        List<Transaction> result = new ListTransactionsService(repository, resolver)
                .list(new ListTransactionsQuery(EMAIL, YearMonth.of(2026, 10), TransactionType.INCOME));

        assertThat(result).isEmpty();
        verify(repository).search(5L, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), TransactionType.INCOME);
    }

    // ---------- GetMonthlySummaryService ----------

    @Test
    void summary_calculaConLosMovimientosDelMes() {
        Transaction income = new Transaction(1L, 5L, TransactionType.INCOME, TransactionCategory.SALARY,
                new BigDecimal("1000000.00"), null, DATE, LocalDateTime.now());
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.search(5L, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), null))
                .thenReturn(List.of(income, existing()));

        MonthlySummary summary = new GetMonthlySummaryService(repository, resolver)
                .getSummary(new MonthlySummaryQuery(EMAIL, YearMonth.of(2026, 10)));

        assertThat(summary.totalIncome()).isEqualByComparingTo("1000000");
        assertThat(summary.totalExpense()).isEqualByComparingTo("50000");
        assertThat(summary.balance()).isEqualByComparingTo("950000");
    }
}