package com.example.demo.task;

import com.example.demo.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /** 某项目下的任务列表 */
    @GetMapping("/projects/{projectId}/tasks")
    public ApiResponse<Page<TaskResponse>> list(
            @AuthenticationPrincipal String username,
            @PathVariable Long projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(taskService.listTasks(
                username, projectId, status, keyword, page, size));
    }

    /** 某项目下新建任务 */
    @PostMapping("/projects/{projectId}/tasks")
    public ApiResponse<TaskResponse> create(
            @AuthenticationPrincipal String username,
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTaskRequest request) {
        return ApiResponse.ok(taskService.create(username, projectId, request));
    }

    /** 查单个任务 */
    @GetMapping("/tasks/{id}")
    public ApiResponse<TaskResponse> get(
            @AuthenticationPrincipal String username,
            @PathVariable Long id) {
        return ApiResponse.ok(taskService.getTask(id, username));
    }

    /** 更新任务 */
    @PutMapping("/tasks/{id}")
    public ApiResponse<TaskResponse> update(
            @AuthenticationPrincipal String username,
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request) {
        return ApiResponse.ok(taskService.update(id, username, request));
    }

    /** 切换任务状态 */
    @PatchMapping("/tasks/{id}/toggle")
    public ApiResponse<TaskResponse> toggle(
            @AuthenticationPrincipal String username,
            @PathVariable Long id) {
        return ApiResponse.ok(taskService.toggleStatus(id, username));
    }

    /** 删除任务 */
    @DeleteMapping("/tasks/{id}")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal String username,
            @PathVariable Long id) {
        taskService.delete(id, username);
        return ApiResponse.ok();
    }
}