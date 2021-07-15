package com.webApp.advice;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Slf4j
public class ServiceAspect {

    @Before(value = "com.webApp.advice.SpringPointcuts.serviceBeanPointcut()")
    public void beforeServiceMethodsLoggingAdvice(JoinPoint joinPoint) {

        log.info("**********************************");

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        log.info("Start of method execution -> {}", methodSignature.getName());

        log.info("**********************************");

    }

    @Around(value = "com.webApp.advice.SpringPointcuts.serviceBeanPointcut()")
    public Object aroundServiceMethodOfMeasuringExecutionTimeAdvice(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.nanoTime();

        Object retVal = joinPoint.proceed();

        long end = System.nanoTime();

        String methodName = joinPoint.getSignature().getName();

        String className = joinPoint.getSignature().getDeclaringTypeName();

        log.info("Exiting method [" + className + "." + methodName + "]; Execution time is: " +
                TimeUnit.NANOSECONDS.toMillis(end - start) + " (ms)");

        log.info("**********************************");

        return retVal;
    }

    @AfterReturning(value = "com.webApp.advice.SpringPointcuts.serviceBeanPointcut()",
            returning = "result")
    public void afterReturningAdvice(JoinPoint joinPoint, Object result) {

        log.info("**********************************");

        log.info("Method executed successfully = " + joinPoint.getSignature().getName());

        log.info("Returning: {}", result);

        log.info("**********************************");
    }

    @AfterThrowing(value = "com.webApp.advice.SpringPointcuts.serviceBeanPointcut()",
            throwing = "ex")
    public void afterThrowingExceptionLoggingAdvice(JoinPoint joinPoint, Throwable ex) {

        log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), ex.getCause() != null ? ex.getCause() : "NULL");
    }
}
