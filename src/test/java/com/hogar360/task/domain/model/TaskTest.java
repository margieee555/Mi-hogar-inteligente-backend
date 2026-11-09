package com.hogar360.task.domain.model;

import com.hogar360.shared.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskTest {

    private static final LocalDate DUE = LocalDate.of(2026, 11, 10);

    private Task nueva() {
        return Task.createNew(5L, "  Limpiar cocina  ", "  Incluye la nevera  ", TaskPriority.HIGH, 3L, DUE);
    }

    @Test
    void createNew_empiezaPendienteYNormalizaLosTextos() {
        Task task = nueva();

        assertThat(task.getId()).isNull();
        assertThat(task.getHouseholdId()).isEqualTo(5L);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(task.getTitle()).isEqualTo("Limpiar cocina");
        assertThat(task.getDescription()).isEqualTo("Incluye la nevera");
        assertThat(task.getAssignedUserId()).isEqualTo(3L);
        assertThat(task.getCompletedAt()).isNull();
        assertThat(task.getCreatedAt()).isNotNull();
    }

    @Test
    void createNew_textosEnBlancoQuedanNulos() {
        Task task = Task.createNew(5L, "Sacar basura", "   ", TaskPriority.LOW, null, null);
        assertThat(task.getDescription()).isNull();
        assertThat(task.getAssignedUserId()).isNull();
        assertThat(task.getDueDate()).isNull();
    }

    @Test
    void createNew_rechazaTituloVacio() {
        assertThatThrownBy(() -> Task.createNew(5L, "   ", null, TaskPriority.LOW, null, null))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.task.title.required");
    }

    @Test
    void createNew_rechazaTituloMuyLargo() {
        String largo = "x".repeat(151);

        assertThatThrownBy(() -> Task.createNew(5L, largo, null, TaskPriority.LOW, null, null))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.task.title.tooLong");
    }

    @Test
    void createNew_rechazaPrioridadNula() {
        assertThatThrownBy(() -> Task.createNew(5L, "Lavar ropa", null, null, null, null))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.task.priority.required");
    }

    @Test
    void changeStatus_completarGuardaLaFechaDeCompletado() {
        Task done = nueva().changeStatus(TaskStatus.COMPLETED);

        assertThat(done.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(done.getCompletedAt()).isNotNull();
        assertThat(done.getTitle()).isEqualTo("Limpiar cocina");
    }

    @Test
    void changeStatus_reabrirBorraLaFechaDeCompletado() {
        Task reopened = nueva().changeStatus(TaskStatus.COMPLETED).changeStatus(TaskStatus.IN_PROGRESS);

        assertThat(reopened.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(reopened.getCompletedAt()).isNull();
    }

    @Test
    void changeStatus_rechazaEstadoNulo() {
        Task task = nueva();

        assertThatThrownBy(() -> task.changeStatus(null))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.task.status.required");
    }
}