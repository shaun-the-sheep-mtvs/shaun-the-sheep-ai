package org.mtvs.backend.aspect;


import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.mtvs.backend.aspect.metrics.UserMetricsRecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class PerformanceAspect{
    private final UserMetricsRecoder userMetricsRecoder;

    @Autowired
    public PerformanceAspect(UserMetricsRecoder userMetricsRecoder) {
        this.userMetricsRecoder = userMetricsRecoder;
    }


    @Around("execution(* org.mtvs.backend.product.*(..))")
    public Object measurePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();

        Timer.Sample sample = null;
        try {
            Object result = joinPoint.proceed();
            sample = userMetricsRecoder.startUserCreationTimer();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            userMetricsRecoder.stopUserCreationTimer(sample,"method", methodName,"status", "success");
        }
    }
// final LogRepository

}
