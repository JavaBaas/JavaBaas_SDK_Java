package com.javabaas.javasdk.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 这个类只是临时处理，有时间了需要改造
 *
 * Created by zangyilin on 2017/12/13.
 */
public class JBAnnotationScanner implements BeanPostProcessor {

    public static Set<Class> classSet;

    private Class originalBeanClass;

    private static final List<Class<? extends Annotation>> annotationsClasses =
            Arrays.asList(JBCloudAnnotation.class, JBHookAnnotation.class);

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        final AtomicBoolean add = new AtomicBoolean();
        ReflectionUtils.doWithMethods(o.getClass(), new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                add.set(true);
            }
        }, new ReflectionUtils.MethodFilter() {
            @Override
            public boolean matches(Method method) {
                for (Class aClass : annotationsClasses) {
                    if (method.isAnnotationPresent(aClass)) {
                        return true;
                    }
                }
                return false;
            }
        });
        if (add.get()) {
            originalBeanClass = o.getClass();
        }
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        if (originalBeanClass != null) {
            if (classSet == null) {
                classSet = new HashSet<>();
            }
            classSet.add(originalBeanClass);
            originalBeanClass = null;
        }
        return o;
    }
}
