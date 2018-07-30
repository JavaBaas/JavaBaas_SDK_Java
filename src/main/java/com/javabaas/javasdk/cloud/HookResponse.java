package com.javabaas.javasdk.cloud;

import java.util.Map;

/**
 * Created by Codi on 2018/7/23.
 */
public class HookResponse extends JBResponse {
    private Map<String, ?> object;

    public Map<String, ?> getObject() {
        return object;
    }

    public void setObject(Map<String, ?> object) {
        this.object = object;
    }
}
