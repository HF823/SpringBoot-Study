package com.example.demo.todo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTodoRequest(
        @NotBlank(message = "标题不能为空")
        @Size(max = 100, message = "标题不能超过100个字符")
        String title
) {
}