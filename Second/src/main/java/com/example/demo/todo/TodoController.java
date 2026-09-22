package com.example.demo.todo;

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
    @GetMapping("/{id}")
    public Todo getById(@PathVariable Long id){
        return todoService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo create(@RequestBody CreateTodoRequest request){
        return todoService.create(request);
    }

    @PutMapping("/{id}")
    public Todo create(@PathVariable Long id,@RequestBody UpdateTodoRequest request){
        return todoService.update(id,request);
    }

    @PatchMapping("/{id}/toggle")
    public Todo toggle(@PathVariable Long id){
        return todoService.toggle(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        todoService.delete(id);
    }



}
