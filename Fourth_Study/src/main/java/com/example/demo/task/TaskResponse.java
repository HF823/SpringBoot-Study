package com.example.demo.task;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        Long projectId,
        String title,
        String description,
        TaskStatus status,
        int priority,
        LocalDateTime dueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TaskResponse from(Task t) {
        return new TaskResponse(
                t.getId(), t.getProjectId(), t.getTitle(), t.getDescription(),
                t.getStatus(), t.getPriority(), t.getDueDate(),
                t.getCreatedAt(), t.getUpdatedAt());
    }
}