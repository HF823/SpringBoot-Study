package com.example.demo.task;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record UpdateTaskRequest(
        @NotBlank(message = "标题不能为空")
        @Size(max = 200, message = "标题不能超过200字符")
        String title,

        @Size(max = 1000, message = "描述不能超过1000字符")
        String description,

        @NotNull(message = "状态不能为空")
        TaskStatus status,

        @Min(1) @Max(5)
        Integer priority,

        LocalDateTime dueDate
) {}