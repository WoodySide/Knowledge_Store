package com.webApp.advice;

import org.aspectj.lang.annotation.Pointcut;

public class SpringPointcuts {

    @Pointcut(value = "within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {}

    @Pointcut(value = "within(com.webApp.repository.*)" +
            " || within(com.webApp.service..*)" +
            " || within(com.webApp.controller..*)")
    public void applicationPackagePointcut() {

    }

    @Pointcut("within(com.webApp.service..*)")
    public void serviceBeanPointcut() {
    }
}
