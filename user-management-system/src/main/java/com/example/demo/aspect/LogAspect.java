package com.example.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LogAspect {

    /** 対象:[UserService]をクラス名に含んでいること */
    @Pointcut("execution(* *..*UserService*.*(..))")
    public void userService() {
    }

    /** 対象:@GetMappingアノテーション */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getMapping() {
    }

    /** 対象:@PostMappingアノテーション */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping() {
    }

    /** サービスの実行前にログ出力する */
    @Before("userService()")
    public void startLog(JoinPoint jp) {
        log.info("メソッド開始: {}", jp.getSignature());
    }

    /** サービスの実行後にログ出力する */
    @After("userService()")
    public void endLog(JoinPoint jp) {
        log.info("メソッド終了: {}", jp.getSignature());
    }

    /** GetMappingとPostMappingの実行前後でログ出力する */
    @Around("getMapping() || postMapping()")
    public Object startLog(ProceedingJoinPoint jp) throws Throwable {
        // 開始ログ出力
        log.info("メソッド開始(Controller): {}", jp.getSignature());
        try {
            // メソッド実行
            Object result = jp.proceed();
            // 終了ログ出力
            log.info("メソッド終了(Controller): {}", jp.getSignature());
            // 実行結果を呼び出し元に返却
            return result;
        } catch (Exception e) {
            // エラーログ出力
            log.error("メソッド異常終了(Controller): {}", jp.getSignature());
            // エラーの再スロー
            throw e;
        }
    }
}
