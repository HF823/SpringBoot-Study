package com.example.demo.project;

import com.example.demo.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ApiResponse<Page<ProjectResponse>> list(
            @AuthenticationPrincipal String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(projectService.listMyProjects(username, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProjectResponse> get(
            @AuthenticationPrincipal String username,
            @PathVariable Long id) {
        return ApiResponse.ok(projectService.getMyProject(id, username));
    }

    @PostMapping
    public ApiResponse<ProjectResponse> create(
            @AuthenticationPrincipal String username,
            @Valid @RequestBody CreateProjectRequest request) {
        return ApiResponse.ok(projectService.create(username, request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ProjectResponse> update(
            @AuthenticationPrincipal String username,
            @PathVariable Long id,
            @Valid @RequestBody CreateProjectRequest request) {
        return ApiResponse.ok(projectService.update(id, username, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal String username,
            @PathVariable Long id) {
        projectService.delete(id, username);
        return ApiResponse.ok();
    }
}