package com.payment.core.common.anno;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    String keyPrefix() default "payment:idempotent";

    long ttlSeconds() default 86400;
}
