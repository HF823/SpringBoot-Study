package com.example.demo.todo;

import com.example.demo.common.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    TodoRepository repository;

    @Mock
    TodoProperties properties;

    @InjectMocks
    TodoService todoService;

    @Test
    void getById_should_throw_when_not_found() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.getById(99L))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_should_save_when_under_limit() {
        when(properties.getMaxSize()).thenReturn(100);
        when(properties.isDefaultCompleted()).thenReturn(false);
        when(repository.count()).thenReturn(0L);
        when(repository.save(any(Todo.class))).thenAnswer(inv -> {
            Todo t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        Todo todo = todoService.create(new CreateTodoRequest("学习测试"));

        assertThat(todo.getId()).isEqualTo(1L);
        assertThat(todo.getTitle()).isEqualTo("学习测试");
        assertThat(todo.isCompleted()).isFalse();
        verify(repository, times(1)).save(any(Todo.class));
    }

    @Test
    void create_should_throw_when_over_limit() {
        when(properties.getMaxSize()).thenReturn(1);
        when(repository.count()).thenReturn(1L);

        assertThatThrownBy(() -> todoService.create(new CreateTodoRequest("x")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("上限");
    }
}