package com.javabaas.javasdk.cloud;

import com.javabaas.javasdk.*;
import com.javabaas.javasdk.annotation.HookEvent;
import com.javabaas.javasdk.callback.JBCloudCallback;
import com.javabaas.javasdk.callback.JBObjectCallback;

import java.util.*;

/**
 * 云方法相处理
 * Created by zangyilin on 2017/11/10.
 */

public class JBCloud {

    /**
     * 初始化云方法（注解方式） 同步
     *
     * @param callbackUrl 云方法地址
     * @return 初始化成功或失败
     * @throws JBException 异常信息
     */
    public static boolean deploy(String callbackUrl) throws JBException {
        CloudSetting cloudSetting = getCloudSetting(callbackUrl);
        deployToJavabaas(cloudSetting, true, new JBCloudCallback() {
            @Override
            public void done(boolean success, Map<String, Object> data, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return true;
    }

    /**
     * 初始化云方法（注解方式） 异步
     *
     * @param callbackUrl 云方法地址
     * @param callback    成功或失败回调
     */
    public static void deployInBackground(String callbackUrl, JBCloudCallback callback) {
        CloudSetting cloudSetting = getCloudSetting(callbackUrl);
        deployToJavabaas(cloudSetting, false, callback);
    }

    /**
     * 初始化云方法 同步
     *
     * @param setting 云方法信息
     * @return 初始化成功或失败
     * @throws JBException 异常信息
     */
    public static boolean deploy(CloudSetting setting) throws JBException {
        deployToJavabaas(setting, true, new JBCloudCallback() {
            @Override
            public void done(boolean success, Map<String, Object> data, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return true;
    }

    /**
     * 初始化云方法 异步
     *
     * @param setting  云方法信息
     * @param callback 成功或失败回调
     */
    public static void deployInBackground(CloudSetting setting, JBCloudCallback callback) {
        deployToJavabaas(setting, false, callback);
    }

    private static void deployToJavabaas(final CloudSetting setting, final boolean sync, final JBCloudCallback callback) {
        String path = JBHttpClient.getCloudDeployPath();
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, null, setting, true, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback != null) {
                    callback.done(true, result.getData(), null);
                }
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, null, error);
                }
            }
        });
    }

    private static CloudSetting getCloudSetting(String remote) {
        CloudSetting cloudSetting = new CloudSetting();
        if (!JBUtils.isEmpty(remote)) {
            cloudSetting.setCustomerHost(remote.endsWith("/") ? remote : remote + "/");
        }
        Set<String> clouds = JB.getInstance().getCloudListeners().keySet();
        List<String> cloudFunctions = new ArrayList<>(clouds);
        cloudSetting.setCloudFunctions(cloudFunctions);
        Map<String, HookSetting> hookSettings = new HashMap<>();

        Set<String> hooks = JB.getInstance().getHookListeners().keySet();
        for (String hookName : hooks) {
            String clazzName = HookSetting.hookClazzName(hookName);
            HookEvent event = HookSetting.hookEvent(hookName);
            if (clazzName == null || event == null) {
                continue;
            }
            HookSetting hookSetting = hookSettings.get(clazzName);
            if (hookSetting == null) {
                hookSetting = new HookSetting();
            }
            switch (event) {
                case BEFORE_INSERT:
                    hookSetting.setBeforeInsert(true);
                    break;
                case AFTER_INSERT:
                    hookSetting.setAfterInsert(true);
                    break;
                case BEFORE_UPDATE:
                    hookSetting.setBeforeUpdate(true);
                    break;
                case AFTER_UPDATE:
                    hookSetting.setAfterUpdate(true);
                    break;
                case BEFORE_DELETE:
                    hookSetting.setBeforeDelete(true);
                    break;
                case AFTER_DELETE:
                    hookSetting.setAfterDelete(true);
                    break;
            }
            hookSettings.put(clazzName, hookSetting);
            cloudSetting.setHookSettings(hookSettings);
        }
        return cloudSetting;
    }

    /**
     * 删除初始化过云方法的信息  同步
     *
     * @return 成功或失败
     * @throws JBException 异常信息
     */
    public static boolean delete() throws JBException {
        deleteFromJavabaas(true, new JBCloudCallback() {
            @Override
            public void done(boolean success, Map<String, Object> data, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return true;
    }

    /**
     * 删除初始化过云方法的信息  异步
     *
     * @param callback 回调信息
     */
    public static void deleteInBackground(JBCloudCallback callback) {
        deleteFromJavabaas(false, callback);
    }

    private static void deleteFromJavabaas(final boolean sync, final JBCloudCallback callback) {
        String path = JBHttpClient.getCloudDeployPath();
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.DELETE, null, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback != null) {
                    callback.done(true, result.getData(), null);
                }
            }

            @Override
            public void onFailure(JBException error) {
                if (callback == null) {
                    return;
                }
                callback.done(false, null, error);
            }
        });
    }

    /**
     * 调用云方法请求 同步
     *
     * @param cloudName 云方法名称
     * @param body      body信息
     * @return 云方法返回信息
     * @throws JBException 异常信息
     */
    public static Map<String, Object> cloud(String cloudName, Map<String, Object> params, Map<String, Object> body) throws JBException {
        final Map<String, Object>[] result = new Map[]{null};
        cloudFromJavabaas(cloudName, params, body, true, new JBCloudCallback() {
            @Override
            public void done(boolean success, Map<String, Object> data, JBException e) {
                if (success) {
                    result[0] = data;
                } else {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return result[0];
    }

    /**
     * 调用云方法请求 异步
     *
     * @param cloudName 云方法名称
     * @param body      body信息
     * @param callback  回调信息
     */
    public static void cloudInBackground(String cloudName, Map<String, Object> params, Map<String, Object> body, JBCloudCallback callback) {
        cloudFromJavabaas(cloudName, params, body, false, callback);
    }

    private static void cloudFromJavabaas(final String cloudName, final Map<String, Object> params, final Map<String, Object> body, final
    boolean sync, final JBCloudCallback callback) {
        String path = JBHttpClient.getCloudPath(cloudName);
        JBHttpParams jbHttpParams = new JBHttpParams(params);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, jbHttpParams, body, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                    return;
                }
                callback.done(true, result.getData(), null);
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, null, error);
                }
            }
        });
    }

}
