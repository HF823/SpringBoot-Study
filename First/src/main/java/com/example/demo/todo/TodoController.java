package com.example.demo.todo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

//可在todo.http里进行新增，修改，删除等操作
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final List<Todo> todos = new CopyOnWriteArrayList<>();
    private final AtomicLong idGen = new AtomicLong(0);

    @GetMapping
    public List<Todo> list() {
        return todos;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getById(@PathVariable Long id) {
        return todos.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Todo> create(@RequestBody CreateTodoRequest request) {
        Todo todo = new Todo(
                idGen.incrementAndGet(),
                request.title(),
                false,
                LocalDateTime.now()
        );
        todos.add(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(todo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> update(@PathVariable Long id,
                                       @RequestBody UpdateTodoRequest request) {
        return todos.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .map(todo -> {
                    todo.setTitle(request.title());
                    todo.setCompleted(request.completed());
                    return ResponseEntity.ok(todo);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean removed = todos.removeIf(t -> t.getId().equals(id));
        if (removed) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}