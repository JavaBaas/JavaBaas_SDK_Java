package com.javabaas.javasdk.annotation.scanner;

import com.javabaas.javasdk.JB;
import com.javabaas.javasdk.annotation.OnCloud;
import com.javabaas.javasdk.cloud.CloudListener;
import com.javabaas.javasdk.cloud.CloudRequest;
import com.javabaas.javasdk.cloud.CloudResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Codi on 2018/7/18.
 */
public class OnCloudScanner implements AnnotationScanner {
    @Override
    public Class<? extends Annotation> getScanAnnotation() {
        return OnCloud.class;
    }

    @Override
    public void addListener(JB jb, final Object object, final Method method, Annotation annot) {
        OnCloud annotation = (OnCloud) annot;
        //云方法名称
        String name = annotation.value();
        if (name.trim().length() == 0) {
            throw new IllegalArgumentException("OnCloud注解必须指定云方法名!");
        }
        final int cloudRequestIndex = paramIndex(method, CloudRequest.class);
        //注入方法
        jb.addCloudListener(name, new CloudListener() {
            @Override
            public CloudResponse onCloud(CloudRequest request) throws Throwable {
                Object[] args = new Object[method.getParameterTypes().length];
                if (cloudRequestIndex != -1) {
                    args[cloudRequestIndex] = request;
                }
                try {
                    return (CloudResponse) method.invoke(object, args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw e.getCause();
                }
            }
        });
    }

    @Override
    public void validate(Method method, Class<?> clazz) {
        if (!CloudResponse.class.equals(method.getReturnType())) {
            //返回类型错误
            throw new IllegalArgumentException("云方法返回类型错误 返回类型必须为CloudResponse " + clazz + "." + method.getName());
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
