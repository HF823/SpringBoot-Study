package com.example.demo.todo;

import com.example.demo.common.BusinessException;
import com.example.demo.common.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoService.class);

    private final TodoRepository repository;
    private final TodoProperties properties;

    public TodoService(TodoRepository repository, TodoProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    public Page<Todo> list(Boolean completed, int page, int size) {
        log.debug("查询 Todo 列表，completed = {}, page = {}, size = {}", completed, page, size);
        if (size > 100) size = 100;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        if (completed == null) {
            return repository.findAll(pageable);
        }
        return repository.findByCompleted(completed, pageable);
    }

    @Cacheable(value = "todo", key = "#id")
    public Todo getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    @Transactional
    public Todo create(CreateTodoRequest request) {
        log.info("创建 Todo，title = {}", request.title());
        if (repository.count() >= properties.getMaxSize()) {
            log.warn("Todo 数量已达上限，当前 = {}, 上限 = {}",
                    repository.count(), properties.getMaxSize());
            throw new BusinessException(ErrorCode.CONFLICT,
                    "Todo 数量已达上限：" + properties.getMaxSize());
        }
        Todo todo = new Todo();
        todo.setTitle(request.title());
        todo.setCompleted(properties.isDefaultCompleted());
        Todo saved = repository.save(todo);
        log.info("创建 Todo 成功，id = {}", saved.getId());
        return saved;
    }

    @CachePut(value = "todo", key = "#id")
    @Transactional
    public Todo update(Long id, UpdateTodoRequest request) {
        Todo todo = getById(id);
        todo.setTitle(request.title());
        todo.setCompleted(request.completed());
        return repository.save(todo);
    }

    @Transactional
    public Todo toggle(Long id) {
        log.info("切换 Todo 状态，id = {}", id);
        Todo todo = getById(id);
        todo.setCompleted(!todo.isCompleted());
        return repository.save(todo);
    }

    @CacheEvict(value = "todo", key = "#id")
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        repository.deleteById(id);
    }


}