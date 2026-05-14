package com.payment.core.common.aspect;

import com.payment.core.common.trace.TraceContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Service 层性能监控 AOP 切面
 */
@Aspect
@Component
public class ServiceLogAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceLogAspect.class);

    /**
     * 定义切点：拦截所有 service.impl 包下的公共方法
     */
    @Pointcut("execution(public * com.payment.core..service.impl..*.*(..))")
    public void serviceLog() {
    }

    @Around("serviceLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        String traceId = TraceContext.getTraceId();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        Object result;
        try {
            // 执行目标业务方法
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("[Service Error] TraceId: {} | Method: {}.{} | Error: {}", 
                    traceId, className, methodName, e.getMessage());
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            long timeCost = endTime - startTime;
            
            // 记录执行时长。如果执行时间超过 500ms，则打印为 WARN 级别（慢方法告警）
            if (timeCost > 500) {
                log.warn("[Service Slow Monitor] TraceId: {} | Method: {}.{} | Time Cost: {} ms", 
                        traceId, className, methodName, timeCost);
            } else {
                log.info("[Service Monitor] TraceId: {} | Method: {}.{} | Time Cost: {} ms", 
                        traceId, className, methodName, timeCost);
            }
        }

        return result;
    }
}
