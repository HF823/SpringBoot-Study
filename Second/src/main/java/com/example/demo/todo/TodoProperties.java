package com.example.demo.todo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "todo")
public class TodoProperties {

    private int maxSize = 100;
    private boolean defaultCompleted = false;

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean isDefaultCompleted() {
        return defaultCompleted;
    }

    public void setDefaultCompleted(boolean defaultCompleted) {
        this.defaultCompleted = defaultCompleted;
    }
}