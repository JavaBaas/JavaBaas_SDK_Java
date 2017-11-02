package com.javabaas.javasdk;

import com.javabaas.javasdk.log.JBLogUtil;
import com.javabaas.javasdk.log.JBLogger;

import java.util.Date;
import java.util.Map;

/**
 * Created by zangyilin on 2017/8/10.
 */
public class JBConfig {
    boolean finishInit;
    String remote;
    String appId;
    String key;
    String masterKey;
    String adminKey;
    long adjustTime;

    public static void init(String remote, String appId, String key) {
        JBConfig.getInstance().initConfig(remote, appId, key, null, null);
        JBConfig.getInstance().updateAdjustTime();
    }

    public static void initAdmin(String remote, String adminKey) {
        JBConfig.getInstance().initConfig(remote, null, null, null, adminKey);
    }

    public static void initMaster(String remote, String appId, String masterKey) {
        JBConfig.getInstance().initConfig(remote, appId, null, masterKey, null);
    }

    public static void useApp(JBApp app) {
        if (app == null) {
            JBConfig.getInstance().removeAppConfig();
        } else {
            JBConfig.getInstance().initConfig(null, app.getId(), app.getKey(), app.getMasterKey(), null);
            JBConfig.getInstance().updateAdjustTime();
        }
    }

    public static void setDebugLogEnabled(boolean enable) {
        JBLogger.instance().setDebugEnabled(enable);
    }

    public void removeAppConfig() {
        this.masterKey = null;
        this.key = null;
        this.appId = null;
}

    private JBConfig() {}

    private static JBConfig INSTANCE = new JBConfig();

    public static JBConfig getInstance() {
        return INSTANCE;
}

    private void initConfig(String remote, String appId, String key, String masterKey, String adminKey) {
        if (!JBUtils.isEmpty(remote)) {
            this.remote = remote.endsWith("/") ? remote : remote + "/";
        }
        if (!JBUtils.isEmpty(appId)) {
            this.appId = appId;
        }
        if (!JBUtils.isEmpty(key)) {
            this.key = key;
        }
        if (!JBUtils.isEmpty(masterKey)) {
            this.masterKey = masterKey;
        }
        if (!JBUtils.isEmpty(adminKey)) {
            this.adminKey = adminKey;
        }
        this.finishInit = true;
    }

    private void updateAdjustTime() {
        long timestamp = new Date().getTime();

        try {
            Map<String, Object> map = JBStatus.getStatus();
            if (map.get("time") != null) {
                long serverTime = (long) map.get("time");
                if (serverTime > 0) {
                    this.adjustTime = timestamp - serverTime;
                }
            } else {
            }
        } catch (JBException e) {
            JBLogUtil.log.w("服务器连接失败，请检查。");
        }
    }
}
