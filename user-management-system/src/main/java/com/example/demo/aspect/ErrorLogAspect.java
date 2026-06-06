package com.example.demo.aspect;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class ErrorLogAspect {

    @Pointcut("bean(*Repository) || bean(*Mapper)")
    public void repositoryLayer() {
    }

    @Pointcut("bean(*Service)")
    public void serviceLayer() {
    }

    @Pointcut("bean(*Controller)")
    public void controllerLayer() {
    }

    @Pointcut("repositoryLayer() || serviceLayer() || controllerLayer()")
    public void applicationLayer() {
    }

    //@AfterThrowing(value = "applicationLayer()", throwing = "ex")
    //public void throwingNull(DataAccessException ex) {
        // 例外処理の内容（ログ出力）
        //log.error("DataAccessExceptionが発生しました");
    //}
    @AfterThrowing(
    	    value = "execution(* com.example.demo..*(..))",
    	    throwing = "ex"
    	)
    	public void throwingNull(DataAccessException ex) {
    	    log.error("DataAccessExceptionが発生しました");
    	}
}
