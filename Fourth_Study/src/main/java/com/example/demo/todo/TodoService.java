package com.example.demo.todo;

import com.example.demo.common.BusinessException;
import com.example.demo.common.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository repository;
    private final TodoProperties properties;

    public TodoService(TodoRepository repository, TodoProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    public Page<Todo> list(Boolean completed, int page, int size) {
        if (size > 100) size = 100;
        PageRequest pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "id"));
        if (completed == null) {
            return repository.findAll(pageable);
        }
        return repository.findByCompleted(completed, pageable);
    }

    public Todo getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    @Transactional
    public Todo create(CreateTodoRequest request) {
        if (repository.count() >= properties.getMaxSize()) {
            throw new BusinessException(ErrorCode.CONFLICT,
                    "Todo 数量已达上限：" + properties.getMaxSize());
        }
        Todo todo = new Todo();
        todo.setTitle(request.title());
        todo.setCompleted(properties.isDefaultCompleted());
        return repository.save(todo);
    }

    @Transactional
    public Todo update(Long id, UpdateTodoRequest request) {
        Todo todo = getById(id);
        todo.setTitle(request.title());
        todo.setCompleted(request.completed());
        return repository.save(todo);
    }

    @Transactional
    public Todo toggle(Long id) {
        Todo todo = getById(id);
        todo.setCompleted(!todo.isCompleted());
        return repository.save(todo);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        repository.deleteById(id);
    }
}