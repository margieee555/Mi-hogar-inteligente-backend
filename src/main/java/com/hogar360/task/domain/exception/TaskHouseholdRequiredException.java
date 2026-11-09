package com.hogar360.task.domain.exception;

public class TaskHouseholdRequiredException extends RuntimeException {

    public TaskHouseholdRequiredException() {
        super("Debes configurar tu hogar antes de usar las tareas");
    }
}