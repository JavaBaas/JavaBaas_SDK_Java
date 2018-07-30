package com.javabaas.javasdk.cloud;

import com.javabaas.javasdk.JB;
import com.javabaas.javasdk.annotation.scanner.AnnotationScanner;
import com.javabaas.javasdk.annotation.scanner.OnCloudScanner;
import com.javabaas.javasdk.annotation.scanner.OnHookScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Codi on 2018/7/18.
 */
public class ScannerEngine {

    private static final List<? extends AnnotationScanner> annotations = Arrays.asList(new OnCloudScanner(), new OnHookScanner());

    /**
     * 扫描含有云方法及钩子注解的方法
     *
     * @param jb     javabaas
     * @param object 含有注解的类
     * @param clazz  注解类型
     * @throws IllegalArgumentException 参数类型不匹配
     */
    public void scan(JB jb, Object object, Class<?> clazz)
            throws IllegalArgumentException {
        Method[] methods = clazz.getDeclaredMethods();

        //扫描所有方法 提取cloud及hook注解方法
        for (Method method : methods) {
            for (AnnotationScanner annotationScanner : annotations) {
                Annotation ann = method.getAnnotation(annotationScanner.getScanAnnotation());
                if (ann != null) {
                    annotationScanner.validate(method, clazz);
                    makeAccessible(method);
                    annotationScanner.addListener(jb, object, method, ann);
                }
            }
        }

    }

    private boolean isEquals(Method method1, Method method2) {
        return method1.getName().equals(method2.getName())
                && method1.getReturnType().equals(method2.getReturnType()) && Arrays.equals(method1.getParameterTypes(),
                method2.getParameterTypes());
    }

    private void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

}
