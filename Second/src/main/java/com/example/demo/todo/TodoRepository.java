package com.example.demo.todo;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {
    List<Todo> findAll();
    List<Todo> findByCompleted(boolean completed);
    Optional<Todo> findById(Long id);
    Todo save(Todo todo);
    boolean deleteById(Long id);
    long count();
}
