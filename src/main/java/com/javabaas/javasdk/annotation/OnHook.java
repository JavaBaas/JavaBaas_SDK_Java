package com.javabaas.javasdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Codi on 2018/7/13.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnHook {
    /**
     * 钩子类名称
     */
    String value();

    /**
     * 钩子类型
     */
    HookEvent event();


}
