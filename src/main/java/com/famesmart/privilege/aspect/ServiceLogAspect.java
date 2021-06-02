package com.famesmart.privilege.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ServiceLogAspect {

    @Around("execution(* com.famesmart.privilege.service..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {

        Class<?> clazz = joinPoint.getTarget().getClass();
        String name = joinPoint.getSignature().getName();
        log.info("====== 开始执行 {}.{} ======", clazz, name);

        long begin = System.currentTimeMillis();
        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();
        long takeTime = end - begin;

        if (takeTime > 3000) {
            log.error("====== 执行结束 {}.{}，耗时：{} 毫秒 ======", clazz, name, takeTime);
        } else if (takeTime > 2000) {
            log.warn("====== 执行结束 {}.{}，耗时：{} 毫秒 ======", clazz, name, takeTime);
        } else {
            log.info("====== 执行结束 {}.{}，耗时：{} 毫秒 ======", clazz, name, takeTime);
        }

        return result;
    }

}
