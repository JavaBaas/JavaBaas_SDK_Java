package com.javabaas.javasdk;

import com.javabaas.javasdk.callback.JBStatusCallback;

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
    String plat;
    String masterKey;
    String adminKey;
    long adjustTime;

    /**
     * 初始化普通权限<br/>
     * 该方法默认设置plat为"cloud",建议服务端初始化时使用该方法
     *
     * @param remote 服务地址,例如"http://127.0.0.1:8080/api"
     * @param appId  应用id
     * @param key    应用普通权限key
     */
    public static void init(String remote, String appId, String key) {
        JBConfig.getInstance().initConfig(remote, appId, key, null, null, "cloud");
        JBConfig.getInstance().updateAdjustTime();
    }

    /**
     * 初始化普通权限
     *
     * @param remote 服务地址,例如"http://127.0.0.1:8080/api"
     * @param appId  应用id
     * @param key    应用普通权限key
     * @param plat   平台:"ios","android","js","cloud"等
     */
    public static void init(String remote, String appId, String key, String plat) {
        JBConfig.getInstance().initConfig(remote, appId, key, null, null, plat);
        JBConfig.getInstance().updateAdjustTime();
    }

    /**
     * 初始化管理员权限
     *
     * @param remote   服务地址,例如"http://127.0.0.1:8080/api"
     * @param adminKey 在服务端配置文件中配置的"baas.auth.key"的key值, 例如"JavaBaas"
     */
    public static void initAdmin(String remote, String adminKey) {
        JBConfig.getInstance().initConfig(remote, null, null, null, adminKey, "cloud");
    }

    /**
     * 初始化超级权限
     *
     * @param remote    服务地址,例如"http://127.0.0.1:8080/api"
     * @param appId     应用id
     * @param masterKey 应用的masterKey
     */
    public static void initMaster(String remote, String appId, String masterKey) {
        JBConfig.getInstance().initConfig(remote, appId, null, masterKey, null, "cloud");
    }

    /**
     * 切换应用
     *
     * @param app 应用信息
     */
    public static void useApp(JBApp app) {
        if (app == null) {
            JBConfig.getInstance().removeAppConfig();
        } else {
            JBConfig.getInstance().initConfig(null, app.getId(), app.getKey(), app.getMasterKey(), null, "cloud");
            JBConfig.getInstance().updateAdjustTime();
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

    private void initConfig(String remote, String appId, String key, String masterKey, String adminKey, String plat) {
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
        if (!JBUtils.isEmpty(plat)) {
            this.plat = plat;
        }
        this.finishInit = true;
    }

    private void updateAdjustTime() {
        long timestamp = new Date().getTime();

        JBStatus.getStatusInBackground(new JBStatusCallback() {
            @Override
            public void done(boolean success, Map<String, Object> status, JBException e) {
                if (success && status.get("time") != null) {
                    long serverTime = (long) status.get("time");
                    if (serverTime > 0) {
                        JBConfig.getInstance().adjustTime = timestamp - serverTime;
                    }
                }
            }
        });
    }
}
