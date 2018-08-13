package com.javabaas.javasdk.cloud;

import java.util.Map;

/**
 * Created by Codi on 2018/7/23.
 */
public class HookResponse extends JBResponse {
    private Map<String, Object> object;

    public Map<String, Object> getObject() {
        return object;
    }

    public void setObject(Map<String, Object> object) {
        this.object = object;
    }
}
