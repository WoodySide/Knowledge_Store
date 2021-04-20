package com.webApp.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;


@Component
@Aspect
@Slf4j
public class LoggingEntityAdvice {

    @Before(value = "com.webApp.aspect.SpringPointcuts.applicationPackagePointcut()")
    public void beforeAdvice(JoinPoint joinPoint) {
        log.info("---------------------------------------------------------");
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        log.info("Start of method execution = " + methodSignature.getName());
    }

    @Around(value = "com.webApp.aspect.SpringPointcuts.springBeanPointcut() " +
            "&& com.webApp.aspect.SpringPointcuts.applicationPackagePointcut()")
    public Object measureMethodExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();
        Object retVal = pjp.proceed();
        long end = System.nanoTime();
        String methodName = pjp.getSignature().getName();
        log.info("Execution of " + methodName + " took = " +
                TimeUnit.NANOSECONDS.toMillis(end - start) + " ms");
        return retVal;
    }

    @Around(value = "com.webApp.aspect.SpringPointcuts.springBeanPointcut() " +
            "&& com.webApp.aspect.SpringPointcuts.applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("Enter: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        }
        try {
            Object result = joinPoint.proceed();
            if (log.isDebugEnabled()) {
                log.debug("Exit: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(), result);
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        }
    }

    @AfterReturning(value = "com.webApp.aspect.SpringPointcuts.applicationPackagePointcut()",
            returning = "result")
    public void afterReturningAdvice(JoinPoint joinPoint, Object result) {
        log.info("---------------------------------------");
        log.info("Method executed successfully = " + joinPoint.getSignature().getName());
        log.info("Returning type = " + result.toString());
    }

    @AfterThrowing(pointcut = "com.webApp.aspect.SpringPointcuts.springBeanPointcut() \" +\n" +
            "\"&& com.webApp.aspect.SpringPointcuts.applicationPackagePointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL");
    }
}