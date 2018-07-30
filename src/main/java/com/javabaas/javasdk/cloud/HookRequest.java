package com.javabaas.javasdk.cloud;

import com.javabaas.javasdk.annotation.HookEvent;

import java.util.Map;

/**
 * Created by Codi on 2018/7/20.
 */
public class HookRequest extends JBRequest {

    private String name;
    private HookEvent event;
    private Map<String, ?> object;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HookEvent getEvent() {
        return event;
    }

    public void setEvent(HookEvent event) {
        this.event = event;
    }

    public Map<String, ?> getObject() {
        return object;
    }

    public void setObject(Map<String, ?> object) {
        this.object = object;
    }

    @Override
    public String requestType() {
        return JBRequest.REQUEST_HOOK;
    }
}
