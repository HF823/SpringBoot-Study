package com.example.demo.todo;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {

    private final TodoRepository repository;
    private final TodoProperties properties;

    public TodoService(TodoRepository repository, TodoProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    public List<Todo> list(Boolean completed){
        if(completed=null){
            return repository.findAll();
        }
        return repository.findByCompleted(completed);
    }

    public Todo getById(long id){
        return repository.findById(id)
                .orElseThrow(() ->new TodoNotFoundException(id));
    }

    public Todo create(CreateTodoRequest request) {
        if (repository.count() >= properties.getMaxSize()) {
            throw new IllegalStateException("Todo 数量已达上限：" + properties.getMaxSize());
        }
        Todo todo = new Todo();
        todo.setTitle(request.title());
        todo.setCompleted(properties.isDefaultCompleted());
        return repository.save(todo);
    }

    public Todo update(Long id, UpdateTodoRequest request) {
        Todo todo = getById(id);
        todo.setTitle(request.title());
        todo.setCompleted(request.completed());
        return repository.save(todo);
    }

    public Todo toggle(Long id) {
        Todo todo = getById(id);
        todo.setCompleted(!todo.isCompleted());
        return repository.save(todo);
    }

    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw new TodoNotFoundException(id);
        }
    }

}
