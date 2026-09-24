package com.example.demo.common;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PageResult<T>(
        List<T> list,
        long total,
        int page,
        int size,
        int totalPages
) {
    public static <E, T> PageResult<T> of(Page<E> page, Function<E, T> mapper) {
        return new PageResult<>(
                page.getContent().stream().map(mapper).toList(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages());
    }
}