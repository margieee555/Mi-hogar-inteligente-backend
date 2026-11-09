package com.hogar360.task.domain.exception;

public class TaskNotFoundException extends RuntimeException {

    private final Long taskId;

    public TaskNotFoundException(Long id) {
        super("No se encontró la tarea con id: " + id);
        this.taskId = id;
    }

    public Long getTaskId() { return taskId; }
}