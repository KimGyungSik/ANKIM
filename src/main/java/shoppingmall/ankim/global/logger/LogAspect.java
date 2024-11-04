package shoppingmall.ankim.global.logger;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

//logging(): 모든 메서드 실행 시간 측정
//beforeLogic(): 컨트롤러와 서비스 메서드 실행 직전 인자 정보 로그
//afterLogic(): 컨트롤러와 서비스 메서드 실행 후 인자 정보 로그
@Aspect
@Slf4j
@Profile("local")
@Component
public class LogAspect {

    @Pointcut("execution(* com.yata.backend..*(..))")
    public void all() {
    }
    @Pointcut("execution(* com.yata.backend..*Controller.*(..))")
    public void controller() {
    }
    @Pointcut("execution(* com.yata.backend..*Service.*(..))")
    public void service(){}
    @Around("all()")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            return result;
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            log.info("log = {}" , joinPoint.getSignature());
            log.info("timeMs = {}", timeMs);
        }
    }
    @Before("controller() || service()")
    public void beforeLogic(JoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        log.info("method = {}", method.getName());

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if(arg != null) {
                log.info("type = {}", arg.getClass().getSimpleName());
                log.info("value = {}", arg);
            }

        }
    }
    @After("controller() || service()")
    public void afterLogic(JoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        log.info("method = {}", method.getName());

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if(arg != null) {
                log.info("type = {}", arg.getClass().getSimpleName());
                log.info("value = {}", arg);
            }

        }
    }



}
