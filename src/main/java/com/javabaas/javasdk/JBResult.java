package com.javabaas.javasdk;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by test on 2017/6/20.
 */
public class JBResult extends HashMap<String, Object> {

    public static int SUCCESS = 0;

    public static JBResult success() {
        return new JBResult(JBCode.SUCCESS);
    }

    public static JBResult error(int code) {
        return new JBResult(code, "");
    }

    private JBResult() {
    }

    public JBResult(JBCode simpleCode) {
        this(simpleCode.getCode(), simpleCode.getMessage());
    }

    public JBResult(int code, String message) {
        put("data", new LinkedHashMap<String, Object>());
        setCode(code);
        setMessage(message);
    }

    public JBResult(String message, Map<? extends String, ?> map) {
        setCode(JBCode.SUCCESS.getCode());
        setMessage(message);
        put("data", new LinkedHashMap<String, Object>());
        putDataAll(map);
    }

    public static JBResult fromError(JBException e) {
        return new JBResult(e.getCode(), e.getMessage());
    }

    public int getCode() {
        return (int) get("code");
    }

    public void setCode(int code) {
        put("code", code);
    }

    public String getMessage() {
        return (String) get("message");
    }

    public void setMessage(String message) {
        put("message", message);
    }

    @SuppressWarnings("unchecked")
    public JBResult putData(String key, Object value) {
        ((LinkedHashMap<String, Object>) get("data")).put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public Object getData(String key) {
        return ((LinkedHashMap<String, Object>) get("data")).get(key);
    }

    public LinkedHashMap<String, Object> getData( ) {
        return (LinkedHashMap<String, Object>) get("data");
    }

    @SuppressWarnings("unchecked")
    public JBResult putDataAll(Map<? extends String, ?> map) {
        ((LinkedHashMap<String, Object>) get("data")).putAll(map);
        return this;
    }
}
