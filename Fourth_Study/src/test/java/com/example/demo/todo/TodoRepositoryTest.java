package com.example.demo.todo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TodoRepositoryTest {

    @Autowired
    TodoRepository repository;

    @Test
    void save_should_assign_id() {
        Todo todo = new Todo();
        todo.setTitle("测试");
        todo.setCompleted(false);

        Todo saved = repository.save(todo);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findByCompleted_should_return_only_completed() {
        Todo a = new Todo(); a.setTitle("a"); a.setCompleted(true);
        Todo b = new Todo(); b.setTitle("b"); b.setCompleted(false);
        repository.save(a);
        repository.save(b);

        Page<Todo> page = repository.findByCompleted(true, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1)
                .extracting(Todo::getTitle)
                .containsExactly("a");
    }
}