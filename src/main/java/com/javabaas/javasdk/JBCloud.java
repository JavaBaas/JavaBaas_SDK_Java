package com.javabaas.javasdk;

import com.javabaas.javasdk.callback.JBCloudCallback;
import com.javabaas.javasdk.callback.JBObjectCallback;

import java.util.List;
import java.util.Map;

/**
 * Created by zangyilin on 2017/11/10.
 */

public class JBCloud {

    public static boolean deploy(JBCloudSetting setting) throws JBException {
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

    public static void deployInBackground(JBCloudSetting setting, JBCloudCallback callback) {
        deployToJavabaas(setting, false, callback);
    }

    private static void deployToJavabaas(final JBCloudSetting setting, final boolean sync, final JBCloudCallback callback) {
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
                    callback.done(false, null, null);
                }
            }
        });
    }

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

    public static Map<String, Object>  cloud(String cloudName, Map<String, Object> body) throws JBException {
        final Map<String, Object>[] result = new Map[]{null};
        cloudFromJavabaas(cloudName, body, true, new JBCloudCallback() {
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

    public static void cloudInBackground(String cloudName, Map<String, Object> body, JBCloudCallback callback) {
        cloudFromJavabaas(cloudName, body, false, callback);
    }

    private static void cloudFromJavabaas(final String cloudName, final Map<String, Object> body, final boolean sync, final JBCloudCallback callback) {
        String path = JBHttpClient.getConfigPath(cloudName);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, null, body, sync, new JBObjectCallback() {
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

    public static class JBCloudSetting {
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

}
