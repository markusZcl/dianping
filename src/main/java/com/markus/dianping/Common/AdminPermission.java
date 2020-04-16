package com.markus.dianping.Common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/9 17:06
 */
@Target({ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface AdminPermission {
    String produceType() default "text/html";
}
