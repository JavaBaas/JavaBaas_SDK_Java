package com.javabaas.javasdk;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zangyilin on 2017/8/11.
 */
public enum JBHttpMethod {
    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    private static final Map<String, JBHttpMethod> mappings = new HashMap<>(8);

    static {
        for (JBHttpMethod httpMethod : values()) {
            mappings.put(httpMethod.name(), httpMethod);
        }
    }

    public static JBHttpMethod resolve(String method) {
        return (method != null ? mappings.get(method) : null);
    }


    public boolean matches(String method) {
        return (this == resolve(method));
    }
}
