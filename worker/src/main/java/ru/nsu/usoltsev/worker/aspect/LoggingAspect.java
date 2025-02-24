package ru.nsu.usoltsev.worker.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LoggingAspect {
    private final ObjectMapper objectMapper;

    @Before("execution(* ru.nsu.usoltsev.worker.controller.*.*(..))")
    public void logBefore(JoinPoint joinPoint) throws Throwable {
        if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
            String body = objectMapper.writeValueAsString(joinPoint.getArgs()[0]);
            log.info("Target method {}.{}, body: {}",
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    body);
        } else {
            log.info("Target method {}.{}",
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName());
        }
    }
}
