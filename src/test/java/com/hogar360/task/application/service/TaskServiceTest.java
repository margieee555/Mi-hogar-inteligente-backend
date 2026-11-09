package com.hogar360.task.application.service;

import com.hogar360.shared.exception.DomainValidationException;
import com.hogar360.task.application.dto.CreateTaskCommand;
import com.hogar360.task.domain.exception.TaskHouseholdRequiredException;
import com.hogar360.task.domain.exception.TaskNotFoundException;
import com.hogar360.task.domain.model.Task;
import com.hogar360.task.domain.model.TaskPriority;
import com.hogar360.task.domain.model.TaskStatus;
import com.hogar360.task.domain.port.out.TaskAssigneeLookupPort;
import com.hogar360.task.domain.port.out.TaskRepositoryPort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskServiceTest {

    private static final String EMAIL = "ana@x.com";

    private final TaskRepositoryPort repository = mock(TaskRepositoryPort.class);
    private final TaskHouseholdResolver resolver = mock(TaskHouseholdResolver.class);
    private final TaskAssigneeLookupPort assignees = mock(TaskAssigneeLookupPort.class);
    private final TaskService service = new TaskService(repository, resolver, assignees);

    private Task existing() {
        return new Task(7L, 5L, "Limpiar cocina", null, TaskStatus.PENDING, TaskPriority.HIGH,
                null, LocalDate.of(2026, 11, 10), LocalDateTime.now(), null);
    }

    @Test
    void create_guardaLaTareaEnElHogarDelUsuarioConSuResponsable() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(assignees.memberNames(5L, EMAIL)).thenReturn(Map.of(3L, "Luis"));
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        service.create(new CreateTaskCommand(EMAIL, " Lavar ropa ", null, TaskPriority.MEDIUM, 3L, null));

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(repository).save(captor.capture());
        Task saved = captor.getValue();
        assertThat(saved.getId()).isNull();
        assertThat(saved.getHouseholdId()).isEqualTo(5L);
        assertThat(saved.getTitle()).isEqualTo("Lavar ropa");
        assertThat(saved.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(saved.getAssignedUserId()).isEqualTo(3L);
    }

    @Test
    void create_rechazaUnResponsableQueNoEsMiembroDelHogar() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(assignees.memberNames(5L, EMAIL)).thenReturn(Map.of(3L, "Luis"));

        assertThatThrownBy(() -> service.create(
                new CreateTaskCommand(EMAIL, "Lavar ropa", null, TaskPriority.LOW, 99L, null)))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.task.assignee.notMember");

        verify(repository, never()).save(any());
    }

    @Test
    void create_sinResponsableNoConsultaLosMiembros() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        service.create(new CreateTaskCommand(EMAIL, "Lavar ropa", null, TaskPriority.LOW, null, null));

        verify(assignees, never()).memberNames(any(), any());
    }

    @Test
    void create_noGuardaSiElUsuarioNoTieneHogar() {
        when(resolver.requireHouseholdId(EMAIL)).thenThrow(new TaskHouseholdRequiredException());

        assertThatThrownBy(() -> service.create(
                new CreateTaskCommand(EMAIL, "Lavar ropa", null, TaskPriority.LOW, null, null)))
                .isInstanceOf(TaskHouseholdRequiredException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void assigneeNames_devuelveLosMiembrosDelHogarDelUsuario() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(assignees.memberNames(5L, EMAIL)).thenReturn(Map.of(3L, "Luis", 4L, "Ana"));

        assertThat(service.assigneeNames(EMAIL)).containsEntry(3L, "Luis").containsEntry(4L, "Ana");
    }

    @Test
    void list_pideLasTareasDelHogarConElFiltro() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByHousehold(5L, TaskStatus.PENDING)).thenReturn(List.of(existing()));

        List<Task> result = service.list(EMAIL, TaskStatus.PENDING);

        assertThat(result).hasSize(1);
    }

    @Test
    void list_sinFiltroPideTodas() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByHousehold(5L, null)).thenReturn(List.of());

        assertThat(service.list(EMAIL, null)).isEmpty();
        verify(repository).findByHousehold(5L, null);
    }

    @Test
    void changeStatus_guardaElNuevoEstado() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(7L, 5L)).thenReturn(Optional.of(existing()));
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = service.changeStatus(7L, EMAIL, TaskStatus.COMPLETED);

        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(result.getCompletedAt()).isNotNull();
    }

    @Test
    void changeStatus_lanzaNotFoundSiLaTareaNoEsDelHogar() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(99L, 5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeStatus(99L, EMAIL, TaskStatus.COMPLETED))
                .isInstanceOf(TaskNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void delete_eliminaLaTareaDelHogar() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(7L, 5L)).thenReturn(Optional.of(existing()));

        service.delete(7L, EMAIL);

        verify(repository).deleteById(7L);
    }

    @Test
    void delete_lanzaNotFoundYNoBorraNada() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(99L, 5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L, EMAIL))
                .isInstanceOf(TaskNotFoundException.class);

        verify(repository, never()).deleteById(any());
    }
}