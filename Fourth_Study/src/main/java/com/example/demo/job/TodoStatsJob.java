package com.example.demo.job;

import com.example.demo.todo.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TodoStatsJob {

    private static final Logger log = LoggerFactory.getLogger(TodoStatsJob.class);

    private final TodoRepository todoRepository;

    public TodoStatsJob(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    /** 每 10 秒执行一次，测试用 */
    @Scheduled(fixedRate = 10_000)
    public void heartbeat() {
        long count = todoRepository.count();
        log.info("【定时任务】心跳，当前 Todo 总数 = {}", count);
    }
}