package com.javabaas.javasdk;

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

    public static void init(String remote, String appId, String key) {
        JBConfig.getInstance().initConfig(remote, appId, key, null, null);
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
        }
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
}
