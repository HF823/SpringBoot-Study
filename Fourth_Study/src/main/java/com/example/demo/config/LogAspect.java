package com.example.demo.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    @Around("execution(* com.example.demo..*Controller.*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();

        log.info("请求开始：{}，参数：{}", method, Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            log.info("请求结束：{}，耗时：{}ms", method, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable e) {
            log.error("请求异常：{}，消息：{}", method, e.getMessage());
            throw e;
        }
    }
}