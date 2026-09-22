package com.example.demo.todo;

public class TodoNotFoundException extends RuntimeException{
    public TodoNotFoundException(Long id){
        super("Todo 不存在，id = "+id);
    }
}
