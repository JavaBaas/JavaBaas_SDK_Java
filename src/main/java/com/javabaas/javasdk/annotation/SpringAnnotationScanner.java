package com.javabaas.javasdk.annotation;

import com.javabaas.javasdk.JB;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Codi on 2018/7/13.
 */
public class SpringAnnotationScanner implements BeanPostProcessor {

    /**
     * 可以被扫描的注解
     */
    private static final List<Class<? extends Annotation>> annotations =
            Arrays.asList(OnCloud.class, OnHook.class);

    private final JB jb;

    private Class originalBeanClass;

    public SpringAnnotationScanner(JB jb) {
        super();
        this.jb = jb;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (originalBeanClass != null) {
            jb.addListeners(bean, originalBeanClass);
            originalBeanClass = null;
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        final AtomicBoolean add = new AtomicBoolean();
        ReflectionUtils.doWithMethods(bean.getClass(),
                new ReflectionUtils.MethodCallback() {
                    @Override
                    public void doWith(Method method) throws IllegalArgumentException {
                        add.set(true);
                    }
                },
                new ReflectionUtils.MethodFilter() {
                    @Override
                    public boolean matches(Method method) {
                        for (Class<? extends Annotation> annotationClass : annotations) {
                            if (method.isAnnotationPresent(annotationClass)) {
                                return true;
                            }
                        }
                        return false;
                    }
                });

        if (add.get()) {
            originalBeanClass = bean.getClass();
        }
        return bean;
    }
}
