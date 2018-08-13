package com.javabaas.javasdk;

import com.javabaas.javasdk.annotation.HookEvent;
import com.javabaas.javasdk.callback.JBStatusCallback;
import com.javabaas.javasdk.cloud.*;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Codi on 2018/7/16.
 */
public class JB {
    private static final JB INSTANCE = new JB();
    private JBConfig config;

    private final ScannerEngine engine = new ScannerEngine();
    private HashMap<String, CloudListener> cloudListeners = new LinkedHashMap<>();
    private HashMap<String, HookListener> hookListeners = new LinkedHashMap<>();

    private JB() {
        config = new JBConfig();
    }

    public HashMap<String, CloudListener> getCloudListeners() {
        return cloudListeners;
    }

    public HashMap<String, HookListener> getHookListeners() {
        return hookListeners;
    }

    /**
     * 初始化普通权限<br/>
     * 该方法默认设置plat为"cloud",建议服务端初始化时使用该方法
     *
     * @param remote 服务地址,例如"http://127.0.0.1:8080/api"
     * @param appId  应用id
     * @param key    应用普通权限key
     */
    public static void init(String remote, String appId, String key) {
        initConfig(remote, appId, key, null, null, "cloud");
        updateAdjustTime();
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
        initConfig(remote, appId, key, null, null, plat);
        updateAdjustTime();
    }

    /**
     * 初始化管理员权限
     *
     * @param remote   服务地址,例如"http://127.0.0.1:8080/api"
     * @param adminKey 在服务端配置文件中配置的"baas.auth.key"的key值, 例如"JavaBaas"
     */
    public static void initAdmin(String remote, String adminKey) {
        initConfig(remote, null, null, null, adminKey, "cloud");
    }

    /**
     * 初始化超级权限
     *
     * @param remote    服务地址,例如"http://127.0.0.1:8080/api"
     * @param appId     应用id
     * @param masterKey 应用的masterKey
     */
    public static void initMaster(String remote, String appId, String masterKey) {
        initConfig(remote, appId, null, masterKey, null, "cloud");
    }

    /**
     * 设置请求超时时间，建议不要随便用
     *
     * @param connectTimeout 链接超时 单位秒 默认10秒
     * @param writeTimeout   读超时   单位秒 默认10秒
     * @param readTimeout    写超时   单位秒 默认10秒
     */
    public static void initHttpTimeout(long connectTimeout, long writeTimeout, long readTimeout) {
        connectTimeout = connectTimeout <= 0 ? 10 : connectTimeout;
        writeTimeout = writeTimeout <= 0 ? 10 : writeTimeout;
        readTimeout = readTimeout <= 0 ? 10 : readTimeout;
        JBHttpClient.setTimeout(connectTimeout, writeTimeout, readTimeout);
    }

    /**
     * 切换应用
     *
     * @param app 应用信息
     */
    public static void useApp(JBApp app) {
        if (app == null) {
            removeAppConfig();
        } else {
            initConfig(null, app.getId(), app.getKey(), app.getMasterKey(), null, "cloud");
            updateAdjustTime();
        }
    }

    public static JB getInstance() {
        return INSTANCE;
    }

    public JBConfig getConfig() {
        return config;
    }

    public void addListeners(Object listeners, Class<?> listenersClass) {
        engine.scan(INSTANCE, listeners, listenersClass);
    }

    public void addCloudListener(String name, CloudListener listener) {
        cloudListeners.put(name, listener);
    }

    public void addHookListener(String name, HookEvent event, HookListener listener) {
        hookListeners.put(HookSetting.hookName(name, event), listener);
    }

    /**
     * JavaBaas云代码请求入口
     *
     * @param requestType 请求类型
     * @param body        请求主体
     */
    public static JBResponse onRequest(String requestType, String body) throws Throwable {
        try {
            //整理请求体
            if (requestType.equals(JBRequest.REQUEST_CLOUD)) {
                //云方法
                CloudRequest cloudRequest = JBUtils.readValue(body, CloudRequest.class);
                return onCloudRequest(cloudRequest);
            } else if (requestType.equals(JBRequest.REQUEST_HOOK)) {
                //钩子
                HookRequest hookRequest = JBUtils.readValue(body, HookRequest.class);
                return onHookRequest(hookRequest);
            }  //请求类型不匹配
        } catch (JBException ignored) {
        }
        return null;
    }

    private static CloudResponse onCloudRequest(CloudRequest cloudRequest) throws Throwable {
        CloudListener listener = INSTANCE.cloudListeners.get(cloudRequest.getName());
        return listener.onCloud(cloudRequest);
    }

    private static HookResponse onHookRequest(HookRequest hookRequest) throws Throwable {
        HookListener listener = INSTANCE.hookListeners.get(HookSetting.hookName(hookRequest.getName(), hookRequest.getEvent()));
        return listener.onHook(hookRequest);
    }


    private static void initConfig(String remote, String appId, String key, String masterKey, String adminKey, String plat) {
        if (!JBUtils.isEmpty(remote)) {
            INSTANCE.config.remote = remote.endsWith("/") ? remote : remote + "/";
        }
        if (!JBUtils.isEmpty(appId)) {
            INSTANCE.config.appId = appId;
        }
        if (!JBUtils.isEmpty(key)) {
            INSTANCE.config.key = key;
        }
        if (!JBUtils.isEmpty(masterKey)) {
            INSTANCE.config.masterKey = masterKey;
        }
        if (!JBUtils.isEmpty(adminKey)) {
            INSTANCE.config.adminKey = adminKey;
        }
        if (!JBUtils.isEmpty(plat)) {
            INSTANCE.config.plat = plat;
        }
        INSTANCE.config.finishInit = true;
    }

    private static void removeAppConfig() {
        INSTANCE.config.masterKey = null;
        INSTANCE.config.key = null;
        INSTANCE.config.appId = null;
    }

    private static void updateAdjustTime() {
        final long timestamp = new Date().getTime();
        JBStatus.getStatusInBackground(new JBStatusCallback() {
            @Override
            public void done(boolean success, Map<String, Object> status, JBException e) {
                if (success && status.get("time") != null) {
                    long serverTime = (long) status.get("time");
                    if (serverTime > 0) {
                        INSTANCE.config.adjustTime = timestamp - serverTime;
                    }
                }
            }
        });
    }


}
