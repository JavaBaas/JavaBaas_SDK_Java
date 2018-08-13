package com.javabaas.javasdk.cloud;

import com.javabaas.javasdk.JBUser;
import com.javabaas.javasdk.JBUtils;

import java.util.Map;

/**
 * Created by Codi on 2018/7/16.
 */
public abstract class JBRequest {
    public static String REQUEST_CLOUD = "1";
    public static String REQUEST_HOOK = "2";

    private String appId;
    private String plat;
    private Map<String, Object> user;
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


    public JBUser getUser() {
        if (user != null) {
            JBUser jbUser = new JBUser();
            JBUtils.copyPropertiesFromMapToJBObject(jbUser, user);
            return jbUser;
        }
        return null;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
