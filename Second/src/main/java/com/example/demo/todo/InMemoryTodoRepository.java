package com.example.demo.todo;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTodoRepository implements TodoRepository {

    private final List<Todo> todos = new CopyOnWriteArrayList<>();
    private final AtomicLong idGen = new AtomicLong(0);

    @Override
    public List<Todo> findAll() {
        return List.copyOf(todos);
    }

    @Override
    public List<Todo> findByCompleted(boolean completed) {
        return todos.stream()
                .filter(t -> t.isCompleted() == completed)
                .toList();
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return todos.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    @Override
    public Todo save(Todo todo) {
        if (todo.getId() == null) {
            todo.setId(idGen.incrementAndGet());
            todo.setCreatedAt(LocalDateTime.now());
            todos.add(todo);
        } else {
            todos.removeIf(t -> t.getId().equals(todo.getId()));
            todos.add(todo);
        }
        return todo;
    }

    @Override
    public boolean deleteById(Long id) {
        return todos.removeIf(t -> t.getId().equals(id));
    }

    @Override
    public long count() {
        return todos.size();
    }
}