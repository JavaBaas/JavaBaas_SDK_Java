package com.javabaas.javasdk;

import com.javabaas.javasdk.callback.*;

import java.util.*;

/**
 * Created by zangyilin on 2017/9/20.
 */
public class JBApp {
    private String id;
    private String name;
    private String key;
    private String masterKey;
    private CloudSetting cloudSetting;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }

    public CloudSetting getCloudSetting() {
        return cloudSetting;
    }

    public void setCloudSetting(CloudSetting cloudSetting) {
        this.cloudSetting = cloudSetting;
    }


    public void save() throws JBException {
        saveAppToJavabaas(true, new JBBooleanCallback() {
            @Override
            public void done(boolean success, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
    }

    public void saveInBackground(JBBooleanCallback callback) {
        saveAppToJavabaas(false, callback);
    }

    private void saveAppToJavabaas(final boolean sync, final JBBooleanCallback callback) {
        if (JBUtils.isEmpty(getName())) {
            callback.done(false, new JBException(JBCode.REQUEST_PARAM_ERROR.getCode(), "app名称不能为空"));
            return;
        }
        String path = JBHttpClient.getAdminPath();
        Map<String, Object> body = getAppMap();
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, null, body, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (result == null || result.get("result") != null) {
                    copyAppFromMap((Map<String, Object>) result.getData().get("result"));
                }

                if (callback != null) {
                    callback.done(true, null);
                }
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, error);
                }
            }
        });
    }

    public void delete() throws JBException {
        deleteAppFromJavabaas(true, new JBBooleanCallback() {
            @Override
            public void done(boolean success, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
    }

    public void deleteInBackground(JBBooleanCallback callback) {
        deleteAppFromJavabaas(false, callback);
    }

    private void deleteAppFromJavabaas(final boolean sync, final JBBooleanCallback callback) {
        if (JBUtils.isEmpty(getId())) {
            callback.done(false, new JBException(JBCode.REQUEST_PARAM_ERROR.getCode(), "id不能为空"));
            return;
        }
        String path = JBHttpClient.getAdminPath(getId());
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.DELETE, null, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback != null) {
                    callback.done(true, null);
                }
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, error);
                }
            }
        });
    }

    public static JBApp get(String appId) throws JBException {
        final JBApp[] apps = {null};
        getFromJavabaas(appId, true, new JBGetAppCallback() {
            @Override
            public void done(boolean success, JBApp app, JBException e) {
                if (success) {
                    apps[0] = app;
                } else {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return apps[0];
    }

    public static void getInBackground(String appId, JBGetAppCallback callback) {
        getFromJavabaas(appId, false, callback);
    }

    private static void getFromJavabaas(final String appId, final boolean sync, final JBGetAppCallback callback) {
        if (JBUtils.isEmpty(appId)) {
            callback.done(false, null, new JBException(JBCode.REQUEST_PARAM_ERROR.getCode(), "id不能为空"));
            return;
        }
        String path = JBHttpClient.getAdminPath(appId);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, null, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                    return;
                }
                if (result.getData() == null || result.getData().get("result") == null) {
                    callback.done(false, null,new JBException(JBCode.APP_NOT_FOUND));
                } else {
                    JBApp app = getAppFromMap((Map<String, Object>) result.getData().get("result"));
                    callback.done(true, app, null);
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

    public void resetKey(int type) throws JBException {
        resetKeyToJavabaas(type, true, new JBBooleanCallback() {
            @Override
            public void done(boolean success, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
    }

    public void resetKeyInBackground(int type, JBBooleanCallback callback) {
        resetKeyToJavabaas(type, false, callback);
    }

    public void resetKeyToJavabaas(final int type, final boolean sync, final JBBooleanCallback callback) {
        if (JBUtils.isEmpty(getId())) {
            callback.done(false, new JBException(JBCode.REQUEST_PARAM_ERROR.getCode(), "app名称不能为空"));
            return;
        }
        String extra;
        if (type == 1) {
            extra = "resetKey";
        } else if (type == 2) {
            extra = "resetMasterKey";
        } else {
            return;
        }
        String path = JBHttpClient.getAdminPath(getId() + "/" + extra);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.PUT, null, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback != null) {
                    callback.done(true, null);
                }
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, error);
                }
            }
        });
    }

    public static List<JBApp> list() throws JBException {
        final List<JBApp>[] lists = new List[]{null};
        listFromJavabaas(true, new JBAppListCallback() {
            @Override
            public void done(boolean success, List<JBApp> list, JBException e) {
                if (success) {
                    lists[0] = list;
                } else {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return lists[0];
    }

    public static void listInBackground(JBAppListCallback callback) {
        listFromJavabaas(false, callback);
    }

    private static void listFromJavabaas(final boolean sync, final JBAppListCallback callback) {
        String path = JBHttpClient.getAdminPath();
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, null, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                List<JBApp> list = getAppListFromMap(result.getData());
                if (callback != null) {
                    callback.done(true, list, null);
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

    public static JBAppExport export(String appId) throws JBException {
        final JBAppExport[] lists = {null};
        exportFromJavabaas(true, appId, new JBAppExportCallback() {
            @Override
            public void done(boolean success, JBAppExport appExport, JBException e) {
                if (success) {
                    lists[0] = appExport;
                } else {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return lists[0];
    }

    public static void exportInBackground(String appId, JBAppExportCallback callback) {
        exportFromJavabaas(false, appId, callback);
    }

    private static void exportFromJavabaas(final boolean sync, final String appId, final JBAppExportCallback callback) {
        String path = JBHttpClient.getAdminPath(appId + "/" + "export");
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, null, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                    return;
                }
                if (result.getData() == null || result.getData().get("result") == null) {
                    callback.done(false, null,new JBException(JBCode.APP_NOT_FOUND));
                } else {
                    JBAppExport appExport = getAppExportFromMap((Map<String, Object>) result.getData().get("result"));
                    callback.done(true, appExport, null);
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

    public static void importData(String data) throws JBException {
        importDataToJavabaas(true, data, new JBBooleanCallback() {
            @Override
            public void done(boolean success, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
    }

    public static void importDataInBackground(String data, JBBooleanCallback callback) {
        importDataToJavabaas(false, data, callback);
    }

    private static void importDataToJavabaas(final boolean sync, final String data, final JBBooleanCallback callback) {
        String path = JBHttpClient.getAdminPath("import");
        Map<String, Object> body;
        try {
            body = JBUtils.readValue(data, Map.class);
        } catch (JBException e) {
            body = null;
        }
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, null, body, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                    return;
                }
                callback.done(true, null);
            }

            @Override
            public void onFailure(JBException error) {
                if (callback == null) {
                    return;
                }
                callback.done(false, error);
            }
        });
    }

    public static List<Long> getApiStat(JBApiStat apiStat) throws JBException {
        final List<Long>[] result = new List[]{null};
        getApiStatFromJavabaas(true, apiStat, new JBApiStatListCallback() {
            @Override
            public void done(boolean success, List<Long> list, JBException e) {
                if (success) {
                    result[0] = list;
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

    public static void getApiStatInBackground(JBApiStat apiStat, JBApiStatListCallback callback) {
        getApiStatFromJavabaas(false, apiStat, callback);
    }

    private static void getApiStatFromJavabaas(final boolean sync, final JBApiStat apiStat, final JBApiStatListCallback callback) {
        String path = JBHttpClient.getApiStatPath();
        JBHttpParams params = getParamsFromApiStat(apiStat);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, params, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                List<Long> list = getApiStatListFromMap(result.getData());
                if (callback != null) {
                    callback.done(true, list, null);
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

    public static void updateAppConfig(JBAppConfig config) throws JBException {
        updateAppConfigToJavabaas(config, true, new JBBooleanCallback() {
            @Override
            public void done(boolean success, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
    }

    public static void updateAppConfigInBackground(JBAppConfig config, JBBooleanCallback callback) {
        updateAppConfigToJavabaas(config, false, callback);
    }

    private static void updateAppConfigToJavabaas(final JBAppConfig config, final boolean sync, final JBBooleanCallback callback) {
        String path = JBHttpClient.getConfigPath("app");
        if (config == null) {
            if (callback != null) {
                callback.done(false, new JBException(JBCode.REQUEST_PARAM_ERROR.getCode(),"Config信息错误"));
            }
            return;
        }
        Map<String, Object> body = new HashMap<>();
        body.put("key", config.getAppConfigKey().getKey());
        body.put("value", config.getValue());
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, null, body, sync, new JBObjectCallback() {
            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, error);
                }
            }

            @Override
            public void onSuccess(JBResult result) {
                if (callback != null) {
                    callback.done(true, null);
                }
            }
        });
    }

    public static String getAppConfig(JBAppConfigKey appConfigKey) throws JBException {
        final String[] result = new String[1];
        getAppConfigFromJavaBaas(appConfigKey, true, new JBGetConfigCallback() {
            @Override
            public void done(boolean success, String value, JBException e) {
                if (success) {
                    result[0] = value;
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

    public static void getAppConfigInBackground(JBAppConfigKey appConfigKey, JBGetConfigCallback callback) {
        getAppConfigFromJavaBaas(appConfigKey, false, callback);
    }

    private static void getAppConfigFromJavaBaas(final JBAppConfigKey appConfigKey, final boolean sync, JBGetConfigCallback callback) {
        String path = JBHttpClient.getConfigPath("app");
        JBHttpParams params = new JBHttpParams();
        if (appConfigKey != null) {
            params.put("key", appConfigKey.getKey());
        }
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, params, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                    return;
                }
                if (result.getData() != null && result.getData().get("result") != null) {
                    Map<String, Object> map = (Map<String, Object>) result.getData().get("result");
                    String value = (String) map.get(appConfigKey.getKey());
                    callback.done(true, value, null);
                } else {
                    callback.done(false, null, new JBException(JBCode.INTERNAL_JSON_ERROR));
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

    private static List<Long> getApiStatListFromMap(Map<String, Object> map) {
        if (map == null || map.get("result") == null) {return new LinkedList<>();}
        List<Long> list = (List<Long>) map.get("result");
        return list;
    }

    private static JBHttpParams getParamsFromApiStat(JBApiStat apiStat) {
        JBHttpParams params = new JBHttpParams();
        if (!JBUtils.isEmpty(apiStat.getClazz())) {
            params.put("clazz", apiStat.getClazz());
        }
        if (apiStat.getMethod() != null && !JBUtils.isEmpty(apiStat.getMethod().value)) {
            params.put("method", apiStat.getMethod().value);
        }
        if (!JBUtils.isEmpty(apiStat.getPlat())) {
            params.put("plat", apiStat.getPlat());
        }
        if (!JBUtils.isEmpty(apiStat.getFrom())) {
            params.put("from", apiStat.getFrom());
        }
        if (!JBUtils.isEmpty(apiStat.getTo())) {
            params.put("to", apiStat.getTo());
        }
        return params;
    }

    private static JBAppExport getAppExportFromMap(Map<String, Object> map) {
        try {
            String exportStr = JBUtils.writeValueAsString(map);
            JBAppExport appExport = JBUtils.readValue(exportStr, JBAppExport.class);
            return appExport;
        } catch (JBException e) {
            return null;
        }

    }

    private static List<JBApp> getAppListFromMap(LinkedHashMap<String, Object> map) {
        if (map == null || map.get("result") == null) {return new LinkedList<>();}
        List<Map<String, Object>> maps = (List<Map<String, Object>>) map.get("result");
        List<JBApp> list = new LinkedList<>();
        for (Map<String, Object> o : maps) {
            list.add(getAppFromMap(o));
        }
        return list;
    }

    private void copyAppFromMap(Map<String, Object> map) {
        try {
            String appStr = JBUtils.writeValueAsString(map);
            JBApp app = JBUtils.readValue(appStr, JBApp.class);
            if (app != null) {
                setId(app.id);
                setName(app.name);
                setKey(app.key);
                setMasterKey(app.masterKey);
                setCloudSetting(app.cloudSetting);
            }
        } catch (JBException e) {}

    }

    private static JBApp getAppFromMap(Map<String, Object> map) {
        try {
            String appStr = JBUtils.writeValueAsString(map);
            JBApp app = JBUtils.readValue(appStr, JBApp.class);
            return app;
        } catch (JBException e) {
            return null;
        }
    }

    private Map<String, Object> getAppMap() {
        Map<String, Object> body = new HashMap<>();
        if (!JBUtils.isEmpty(id)) {
            body.put("id", id);
        }
        if (!JBUtils.isEmpty(name)) {
            body.put("name", name);
        }
        if (!JBUtils.isEmpty(key)) {
            body.put("key", key);
        }
        if (!JBUtils.isEmpty(masterKey)) {
            body.put("masterKey", masterKey);
        }
        if (cloudSetting != null) {
            body.put("cloudSetting", cloudSetting);
        }
        return body;
    }

    @Override
    public String toString() {
        try {
            return JBUtils.writeValueAsString(this);
        } catch (JBException e) {
            return "";
        }
    }

    public static class CloudSetting {
        private String customerHost;
        private List<String> cloudFunctions;
        private Map<String, HookSetting> hookSettings;

        public String getCustomerHost() {
            return customerHost;
        }

        public void setCustomerHost(String customerHost) {
            this.customerHost = customerHost;
        }

        public List<String> getCloudFunctions() {
            return cloudFunctions;
        }

        public void setCloudFunctions(List<String> cloudFunctions) {
            this.cloudFunctions = cloudFunctions;
        }

        public Map<String, HookSetting> getHookSettings() {
            return hookSettings;
        }

        public void setHookSettings(Map<String, HookSetting> hookSettings) {
            this.hookSettings = hookSettings;
        }

        public HookSetting getHookSetting(String name) {
            return hookSettings == null ? null : hookSettings.get(name);
        }

        public boolean hasFunction(String name) {
            if (cloudFunctions == null) {
                return false;
            } else {
                for (String function : cloudFunctions) {
                    if (function.equals(name)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }
    public static class HookSetting {
        private boolean beforeInsert;
        private boolean afterInsert;
        private boolean beforeUpdate;
        private boolean afterUpdate;
        private boolean beforeDelete;
        private boolean afterDelete;

        public HookSetting() {
        }

        public HookSetting(boolean enable) {
            this.beforeInsert = enable;
            this.afterInsert = enable;
            this.beforeUpdate = enable;
            this.afterUpdate = enable;
            this.beforeDelete = enable;
            this.afterDelete = enable;
        }

        public HookSetting(boolean beforeInsert, boolean afterInsert, boolean beforeUpdate, boolean afterUpdate, boolean beforeDelete,
                           boolean afterDelete) {
            this.beforeInsert = beforeInsert;
            this.afterInsert = afterInsert;
            this.beforeUpdate = beforeUpdate;
            this.afterUpdate = afterUpdate;
            this.beforeDelete = beforeDelete;
            this.afterDelete = afterDelete;
        }

        public boolean isBeforeInsert() {
            return beforeInsert;
        }

        public void setBeforeInsert(boolean beforeInsert) {
            this.beforeInsert = beforeInsert;
        }

        public boolean isAfterInsert() {
            return afterInsert;
        }

        public void setAfterInsert(boolean afterInsert) {
            this.afterInsert = afterInsert;
        }

        public boolean isBeforeUpdate() {
            return beforeUpdate;
        }

        public void setBeforeUpdate(boolean beforeUpdate) {
            this.beforeUpdate = beforeUpdate;
        }

        public boolean isAfterUpdate() {
            return afterUpdate;
        }

        public void setAfterUpdate(boolean afterUpdate) {
            this.afterUpdate = afterUpdate;
        }

        public boolean isBeforeDelete() {
            return beforeDelete;
        }

        public void setBeforeDelete(boolean beforeDelete) {
            this.beforeDelete = beforeDelete;
        }

        public boolean isAfterDelete() {
            return afterDelete;
        }

        public void setAfterDelete(boolean afterDelete) {
            this.afterDelete = afterDelete;
        }
    }

    public static class JBAppExport {
        private String id;
        private String name;
        private String key;
        private String masterKey;
        private CloudSetting cloudSetting;
        private List<JBClazz.JBClazzExport> clazzs;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getMasterKey() {
            return masterKey;
        }

        public void setMasterKey(String masterKey) {
            this.masterKey = masterKey;
        }

        public CloudSetting getCloudSetting() {
            return cloudSetting;
        }

        public void setCloudSetting(CloudSetting cloudSetting) {
            this.cloudSetting = cloudSetting;
        }

        public List<JBClazz.JBClazzExport> getClazzs() {
            return clazzs;
        }

        public void setClazzs(List<JBClazz.JBClazzExport> clazzs) {
            this.clazzs = clazzs;
        }

    }

    public static class JBApiStat {
        private String plat;
        private String clazz;
        private JBApiMethod method;
        private String from;
        private String to;

        public JBApiStat(String plat, String clazz, JBApiMethod method, String from, String to) {
            this.plat = plat;
            this.clazz = clazz;
            this.method = method;
            this.from = from;
            this.to = to;
        }

        public JBApiMethod getMethod() {
            return method;
        }

        public void setMethod(JBApiMethod method) {
            this.method = method;
        }

        public String getPlat() {
            return plat;
        }

        public void setPlat(String plat) {
            this.plat = plat;
        }

        public String getClazz() {
            return clazz;
        }

        public void setClazz(String clazz) {
            this.clazz = clazz;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }
    }
    public static class JBAppConfig {
        private JBAppConfigKey appConfigKey;
        private String value;

        public JBAppConfigKey getAppConfigKey() {
            return appConfigKey;
        }

        public void setAppConfigKey(JBAppConfigKey appConfigKey) {
            this.appConfigKey = appConfigKey;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public enum JBAppConfigKey {
        // 短信相关
        SMS_TRY_LIMIT("baas.sms.tryLimit", "短信_重试次数", "5"),
        SMS_HANDLER("baas.sms.handler", "短信_发送器", "aliyun"),
        SMS_HANDLER_ALIYUN_KEY("baas.sms.handler.aliyun.key", "短信_阿里云key", ""),
        SMS_HANDLER_ALIYUN_SECRET("baas.sms.handler.aliyun.secret", "短信_阿里云secret", ""),
        SMS_CODE_TEMPLATE_ID("baas.sms.codeTemplateId", "短信_验证码模版id", ""),
        SMS_SIGN_NAME("baas.sms.signName", "短信_签名", ""),
        SMS_SEND_INTERVAL("baas.sms.interval", "短信_发送间隔", "60"),
        // 推送相关
        PUSH_HANDLER("baas.push.handler","推送_处理handler","jpush"),
        PUSH_HANDLER_JPUSH_KEY("baas.push.handler.jpush.key", "推送_极光推送key", ""),
        PUSH_HANDLER_JPUSH_SECRET("baas.push.handler.jpush.secret", "推送_极光推送secret", ""),
        // 文件存储相关
        FILE_HANDLER("baas.file.handler","文件_处理handler","qiniu"),
        FILE_HANDLER_QINIU_AK("baas.file.handler.qiniu.ak", "文件_七牛ak", ""),
        FILE_HANDLER_QINIU_SK("baas.file.handler.qiniu.sk", "文件_七牛sk", ""),
        FILE_HANDLER_QINIU_BUCKET("baas.file.handler.qiniu.bucket", "文件_七牛bucket", ""),
        FILE_HANDLER_QINIU_PIPELINE("baas.file.handler.qiniu.pipeline", "文件_七牛pipeline", ""),
        FILE_HANDLER_QINIU_URL("baas.file.handler.qiniu.url", "文件_七牛url", ""),
        ///////// huadong/huabei/huanan/beimei/auto   一共四个zone，如果无值或者值没有匹配项，则认为是auto
        FILE_HANDLER_QINIU_ZONE("baas.file.handler.qiniu.zone", "文件_七牛zone", ""),
        // 微信小程序
        WEBAPP_APPID("baas.webapp.appid", "微信小程序_appid", ""),
        WEBAPP_SECRET("baas.webapp.secret", "微信小程序_secret", "");

        private String key;
        private String name;
        private String defaultValue;

        public String getKey() {
            return key;
        }

        public String getName() {
            return name;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public static JBAppConfigKey getConfig(String key) {
            if (JBUtils.isEmpty(key)) {
                return null;
            } else {
                for (JBAppConfigKey appConfigKey : JBAppConfigKey.values()) {
                    if (key.equals(appConfigKey.getKey())) {
                        return appConfigKey;
                    }
                }
            }
            return null;
        }

        public static String getDefaultValue(String key) {
            JBAppConfigKey config = getConfig(key);
            if (config != null) {
                return config.getDefaultValue();
            } else {
                return null;
            }
        }

        JBAppConfigKey(String key, String name, String defaultValue) {
            this.key = key;
            this.name = name;
            this.defaultValue = defaultValue;
        }
    }

    public enum JBApiMethod {

        INSERT("insert"),
        UPDATE("update"),
        FIND("find"),
        DELETE("delete");

        private String value;

        JBApiMethod(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static JBApiMethod get(String value) {
            JBApiMethod[] methods = JBApiMethod.class.getEnumConstants();
            for (JBApiMethod method : methods) {
                if (method.value.equals(value)) {
                    return method;
                }
            }
            return null;
        }
    }
}
