package com.payment.core.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.core.common.trace.TraceContext;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * 全局接口统一日志与性能监控 AOP 切面
 */
@Aspect
@Component
public class WebLogAspect {

    private static final Logger log = LoggerFactory.getLogger(WebLogAspect.class);
    private final ObjectMapper objectMapper;

    public WebLogAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 定义切点：拦截所有 Controller 的所有方法
     */
    @Pointcut("execution(public * com.payment.core..controller.*.*(..))")
    public void webLog() {
    }

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取当前请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        // 生成或获取 TraceId
        String traceId = TraceContext.getTraceId();
        if (traceId == null) {
            traceId = UUID.randomUUID().toString().replace("-", "");
            TraceContext.setTraceId(traceId);
        }

        if (request != null) {
            log.info("========================================== Start ==========================================");
            log.info("Trace ID       : {}", traceId);
            log.info("URL            : {}", request.getRequestURL().toString());
            log.info("HTTP Method    : {}", request.getMethod());
            log.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            log.info("IP             : {}", request.getRemoteAddr());
            try {
                log.info("Request Args   : {}", objectMapper.writeValueAsString(joinPoint.getArgs()));
            } catch (Exception e) {
                log.info("Request Args   : [Error parsing args]");
            }
        }

        Object result;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("Exception in method {}: {}", joinPoint.getSignature().getName(), e.getMessage());
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            if (request != null) {
                log.info("Time Cost      : {} ms", (endTime - startTime));
                log.info("=========================================== End ===========================================");
            }
            TraceContext.clear();
        }

        return result;
    }
}
