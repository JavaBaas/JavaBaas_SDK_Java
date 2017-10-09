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
    private AppAccounts appAccounts;

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

    public AppAccounts getAppAccounts() {
        return appAccounts;
    }

    public void setAppAccounts(AppAccounts appAccounts) {
        this.appAccounts = appAccounts;
    }


    public void save() throws JBException {
        saveAppToJavabaas(true, new JBSaveCallback() {
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

    public void saveInBackground(JBSaveCallback callback) {
        saveAppToJavabaas(false, callback);
    }

    private void saveAppToJavabaas(final boolean sync, final JBSaveCallback callback) {
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
        deleteAppFromJavabaas(true, new JBDeleteCallback() {
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

    public void deleteInBackground(JBDeleteCallback callback) {
        deleteAppFromJavabaas(false, callback);
    }

    private void deleteAppFromJavabaas(final boolean sync, final JBDeleteCallback callback) {
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
        resetKeyToJavabaas(type, true, new JBUpdateCallback() {
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

    public void resetKey(int type, JBUpdateCallback callback) {
        resetKeyToJavabaas(type, false, callback);
    }

    public void resetKeyToJavabaas(final int type, final boolean sync, final JBUpdateCallback callback) {
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

    private static List<JBApp> getAppListFromMap(LinkedHashMap<String, Object> map) {
        if (map == null || map.get("result") == null) {return new LinkedList<>();}
        List<Map<String, Object>> maps = (List<Map<String, Object>>) map.get("result");
        List<JBApp> list = new LinkedList<>();
        maps.forEach(o -> list.add(getAppFromMap(o)));
        return list;
    }

    private void copyAppFromMap(Map<String, Object> map) {
        String appStr = JBUtils.writeValueAsString(map);
        JBApp app = JBUtils.readValue(appStr, JBApp.class);
        if (app != null) {
            setId(app.id);
            setName(app.name);
            setKey(app.key);
            setMasterKey(app.masterKey);
            setAppAccounts(app.appAccounts);
            setCloudSetting(app.cloudSetting);
        }
    }

    private static JBApp getAppFromMap(Map<String, Object> map) {
        String appStr = JBUtils.writeValueAsString(map);
        JBApp app = JBUtils.readValue(appStr, JBApp.class);
        return app;
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
        if (appAccounts != null) {
            body.put("appAccounts", appAccounts);
        }
        return body;
    }

    @Override
    public String toString() {
        return JBUtils.writeValueAsString(this);
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

    public static class AppAccounts extends LinkedHashMap<String, Account> {
        public AppAccounts() {
            super();
        }

        public Account getAccount(AccountType accountType) {
            return get(accountType.getValue());
        }

        public void setAccount(AccountType accountType, Account account) {
            put(accountType.getValue(), account);
        }
    }

    public static class Account extends LinkedHashMap<String, Object> {
        public Account() {
            super();
        }

        public void setKey(String id) {
            put("key", id);
        }

        public String getKey() {
            return (String) get("key");
        }

        public void setSecret(String id) {
            put("secret", id);
        }

        public String getSecret() {
            return (String) get("secret");
        }
    }

    public static enum AccountType {
        PUSH(1, "push"),
        WEBAPP(2, "webapp");

        private int code;
        private String value;

        AccountType(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return this.code;
        }

        public String getValue() {
            return this.value;
        }

        public static AccountType getType(int code) {
            for (AccountType accountType : AccountType.values()) {
                if (accountType.code == code) {
                    return accountType;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return super.toString() + " code:" + this.code + " value:" + this.value;
        }
    }

}
