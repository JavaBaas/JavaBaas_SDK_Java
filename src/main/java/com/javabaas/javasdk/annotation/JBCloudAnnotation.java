package com.javabaas.javasdk.annotation;

import java.lang.annotation.*;

/**
 * Created by zangyilin on 2017/11/29.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JBCloudAnnotation {
    String value();
}
