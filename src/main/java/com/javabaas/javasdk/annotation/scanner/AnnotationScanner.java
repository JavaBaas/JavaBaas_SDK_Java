package com.javabaas.javasdk.annotation.scanner;

import com.javabaas.javasdk.JB;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by Codi on 2018/7/18.
 */
public interface AnnotationScanner {

    Class<? extends Annotation> getScanAnnotation();

    void addListener(JB jb, Object object, Method method, Annotation annotation);

    /**
     * 检查注解方法参数是否符合要求
     *
     * @param method 方法
     * @param clazz  类型
     */
    void validate(Method method, Class<?> clazz);

}
