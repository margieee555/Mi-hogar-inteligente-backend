package com.hogar360.task.infrastructure.adapter.in.web;
import java.util.Map;
import com.hogar360.task.application.dto.CreateTaskCommand;
import com.hogar360.task.domain.model.Task;
import com.hogar360.task.domain.model.TaskStatus;
import com.hogar360.task.domain.port.in.ManageTasksUseCase;
import com.hogar360.task.infrastructure.adapter.in.web.dto.ChangeTaskStatusRequest;
import com.hogar360.task.infrastructure.adapter.in.web.dto.TaskRequest;
import com.hogar360.task.infrastructure.adapter.in.web.dto.TaskResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Adaptador de entrada HTTP de tareas. Protegido por el JwtAuthenticationFilter:
 * authentication.getName() es el email del JWT.
 */
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final ManageTasksUseCase manageTasksUseCase;

    public TaskController(ManageTasksUseCase manageTasksUseCase) {
        this.manageTasksUseCase = manageTasksUseCase;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> list(@RequestParam(required = false) TaskStatus status,
                                                   Authentication authentication) {
        String email = authentication.getName();
        Map<Long, String> members = manageTasksUseCase.assigneeNames(email);
        List<TaskResponse> body = manageTasksUseCase.list(email, status)
                .stream()
                .map(task -> TaskResponse.from(task, members))
                .toList();
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request,
                                               Authentication authentication) {
        String email = authentication.getName();
        Task task = manageTasksUseCase.create(new CreateTaskCommand(
                email, request.title(), request.description(),
                request.priority(), request.assignedUserId(), request.dueDate()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TaskResponse.from(task, manageTasksUseCase.assigneeNames(email)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> changeStatus(@PathVariable Long id,
                                                     @Valid @RequestBody ChangeTaskStatusRequest request,
                                                     Authentication authentication) {
        String email = authentication.getName();
        Task task = manageTasksUseCase.changeStatus(id, email, request.status());
        return ResponseEntity.ok(TaskResponse.from(task, manageTasksUseCase.assigneeNames(email)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        manageTasksUseCase.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
