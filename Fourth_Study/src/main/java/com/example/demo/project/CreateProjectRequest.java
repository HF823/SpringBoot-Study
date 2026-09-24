package com.example.demo.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
        @NotBlank(message = "项目名不能为空")
        @Size(max = 100, message = "项目名不能超过100字符")
        String name,

        @Size(max = 500, message = "描述不能超过500字符")
        String description
) {}