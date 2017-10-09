package com.javabaas.javasdk;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zangyilin on 2017/9/6.
 */
public class JBPointer extends LinkedHashMap<String, Object> {
    public JBPointer() {
        super();
    }

    public JBPointer(Map<String, Object> m) {
        super(m);
    }

    public JBPointer(String className, String id) {
        setType();
        setClassName(className);
        setId(id);
    }

    public void setType() {
        put("__type", "Pointer");
    }

    public String getType() {
        return (String) get("__type");
    }

    public void setClassName(String className) {
        put("className", className);
    }

    public String getClassName() {
        return (String) get("className");
    }

    public void setId(String id) {
        put("_id", id);
    }

    public String getId() {
        return (String) get("_id");
    }
}
