package com.example.demo.task;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record CreateTaskRequest(
        @NotBlank(message = "标题不能为空")
        @Size(max = 200, message = "标题不能超过200字符")
        String title,

        @Size(max = 1000, message = "描述不能超过1000字符")
        String description,

        @Min(value = 1, message = "优先级最低1")
        @Max(value = 5, message = "优先级最高5")
        Integer priority,

        @Future(message = "截止时间必须在将来")
        LocalDateTime dueDate
) {}