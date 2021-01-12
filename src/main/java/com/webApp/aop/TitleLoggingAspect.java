package com.webApp.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Aspect
public class TitleLoggingAspect {

    private final Logger logger = Logger.getLogger(TitleLoggingAspect.class.getName());

    @Before(value = "execution (* findAll*())")
    public void beforeFindAllTitlesAdvice() {
        logger.log(Level.INFO, "--------------------------------------");
        logger.log(Level.INFO,"The start of method execution: find all titles" );
    }

    @Before(value = "execution(* find*(..))")
    public void beforeFindTitleByIdAdvice() {
        logger.log(Level.INFO, "--------------------------------------");
        logger.log(Level.INFO,"The start of method execution: find title by id" );
    }
}
