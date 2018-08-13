package com.javabaas.javasdk.annotation.scanner;

import com.javabaas.javasdk.JB;
import com.javabaas.javasdk.annotation.HookEvent;
import com.javabaas.javasdk.annotation.OnHook;
import com.javabaas.javasdk.cloud.HookListener;
import com.javabaas.javasdk.cloud.HookRequest;
import com.javabaas.javasdk.cloud.HookResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Codi on 2018/7/18.
 */
public class OnHookScanner implements AnnotationScanner {
    @Override
    public Class<? extends Annotation> getScanAnnotation() {
        return OnHook.class;
    }

    @Override
    public void addListener(JB jb, final Object object, final Method method, Annotation annot) {
        OnHook annotation = (OnHook) annot;
        //云方法名称
        String name = annotation.value();
        if (name.trim().length() == 0) {
            throw new IllegalArgumentException("OnCloud注解必须指定云方法名!");
        }
        HookEvent event = annotation.event();
        final int HookRequestIndex = paramIndex(method, HookRequest.class);
        //注入方法
        jb.addHookListener(name, event, new HookListener() {
            @Override
            public HookResponse onHook(HookRequest hookRequest) throws Throwable {
                Object[] args = new Object[method.getParameterTypes().length];
                if (HookRequestIndex != -1) {
                    args[HookRequestIndex] = hookRequest;
                }
                try {
                    return (HookResponse) method.invoke(object, args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw e.getCause();
                }
            }
        });
    }

    @Override
    public void validate(Method method, Class<?> clazz) {
        if (!HookResponse.class.equals(method.getReturnType())) {
            //返回类型错误
            throw new IllegalArgumentException("钩子返回类型错误 返回类型必须为HookResponse " + clazz + "." + method.getName());
        }
    }

    private int paramIndex(Method method, Class<?> clazz) {
        int index = 0;
        for (Class<?> type : method.getParameterTypes()) {
            if (type.equals(clazz)) {
                return index;
            }
            index++;
        }
        return -1;
    }
}
