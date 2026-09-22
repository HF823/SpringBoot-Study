package com.example.demo.todo;

import com.example.demo.common.BusinessException;
import com.example.demo.common.ErrorCode;

public class TodoNotFoundException extends BusinessException {

    public TodoNotFoundException(Long id) {
        super(ErrorCode.NOT_FOUND, "Todo 不存在，id = " + id);
    }
}