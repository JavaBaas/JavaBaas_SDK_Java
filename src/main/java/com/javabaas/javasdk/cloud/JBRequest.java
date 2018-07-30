package com.javabaas.javasdk.cloud;

import java.util.Map;

/**
 * Created by Codi on 2018/7/16.
 */
public abstract class JBRequest {
    public static String REQUEST_CLOUD = "1";
    public static String REQUEST_HOOK = "2";

    private String appId;
    private String plat;
    private Map<String, ?> user;
    private String timestamp;

    public abstract String requestType();

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPlat() {
        return plat;
    }

    public void setPlat(String plat) {
        this.plat = plat;
    }

    public Map<String, ?> getUser() {
        return user;
    }

    public void setUser(Map<String, ?> user) {
        this.user = user;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
