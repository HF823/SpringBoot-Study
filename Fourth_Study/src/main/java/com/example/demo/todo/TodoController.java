package com.example.demo.todo;

import com.example.demo.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService){
        this.todoService=todoService;
    }

    @GetMapping
    public ApiResponse<Page<Todo>> list(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(todoService.list(completed, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Todo> getById(@PathVariable Long id) {
        return ApiResponse.ok(todoService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Todo> create(@Valid @RequestBody CreateTodoRequest request) {
        return ApiResponse.ok(todoService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Todo> update(@PathVariable Long id,
                                    @Valid @RequestBody UpdateTodoRequest request) {
        return ApiResponse.ok(todoService.update(id, request));
    }

    @PatchMapping("/{id}/toggle")
    public ApiResponse<Todo> toggle(@PathVariable Long id) {
        return ApiResponse.ok(todoService.toggle(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        todoService.delete(id);
    }



}
