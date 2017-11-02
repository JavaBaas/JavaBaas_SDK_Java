package com.javabaas.javasdk;

import com.javabaas.javasdk.callback.*;

import java.util.*;

/**
 * Created by zangyilin on 2017/9/21.
 */
public class JBClazz {
    private String id;
    private JBApp app;
    private String name;
    private JBClazzAcl acl;
    private boolean internal;
    private long count;

    public JBClazz() {
    }

    public JBClazz(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JBApp getApp() {
        return app;
    }

    public void setApp(JBApp app) {
        this.app = app;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JBClazzAcl getAcl() {
        return acl;
    }

    public void setAcl(JBClazzAcl acl) {
        this.acl = acl;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void save() throws JBException {
        saveToJavabaas(true, new JBSaveCallback() {
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

    public void saveInBackdround(JBSaveCallback callback) {
        saveToJavabaas(false, callback);
    }

    private void saveToJavabaas(final boolean sync, final JBSaveCallback callback) {
        String path = JBHttpClient.getClazzPath();
        Map<String, Object> body = getClazzMap();
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, null, body, sync, new JBObjectCallback() {
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

    public void delete() throws JBException {
        deleteClazzFromJavabaas(true, new JBDeleteCallback() {
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
        deleteClazzFromJavabaas(false, callback);
    }

    private void deleteClazzFromJavabaas(final boolean sync, final JBDeleteCallback callback) {
        if (JBUtils.isEmpty(getName())) {
            callback.done(false, new JBException(JBCode.REQUEST_PARAM_ERROR.getCode(), "className不能为空"));
            return;
        }
        String path = JBHttpClient.getClazzPath(getName());
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.DELETE, null, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback != null) {
                    callback.done(true, null);
                }
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

    public static JBClazz get(String name) throws JBException {
        final JBClazz[] clazzes = {null};
        getFromJavabaas(name, true, new JBGetClazzCallback() {
            @Override
            public void done(boolean success, JBClazz clazz, JBException e) {
                if (success) {
                    clazzes[0] = clazz;
                } else {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return clazzes[0];
    }

    public static void getInBackground(String name, JBGetClazzCallback callback) {
        getFromJavabaas(name, false, callback);
    }

    private static void getFromJavabaas(final String name, final boolean sync, final JBGetClazzCallback callback) {
        if (JBUtils.isEmpty(name)) {
            callback.done(false, null, new JBException(JBCode.REQUEST_PARAM_ERROR.getCode(), "name不能为空"));
            return;
        }
        String path = JBHttpClient.getClazzPath(name);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, null, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                JBClazz clazz = copyClazzFromResultData(result.getData());
                if (callback != null) {
                    callback.done(true, clazz,null);
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

    public void updateClazzAcl() throws JBException {
        updateClazzAclToJavabaas(true, new JBUpdateCallback() {
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

    public void updateClazzAclInBackground(JBUpdateCallback callback) {
        updateClazzAclToJavabaas(false, callback);
    }

    private void updateClazzAclToJavabaas(final boolean sync, final JBUpdateCallback callback) {
        if (JBUtils.isEmpty(getName())) {
            callback.done(false, new JBException(JBCode.REQUEST_PARAM_ERROR.getCode(), "className不能为空"));
            return;
        }
        String path = JBHttpClient.getClazzPath(getName() + "/acl");
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, null, getAcl(), sync, new JBObjectCallback() {
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

    private Map<String, Object> getClazzMap() {
        Map<String, Object> map = new HashMap<>();
        if (!JBUtils.isEmpty(getId())) {
            map.put("id", getId());
        }
        if (!JBUtils.isEmpty(getName())) {
            map.put("name", getName());
        }
        if (getAcl() != null) {
            map.put("acl", getAcl());
        }
        return map;
    }

    public static List<JBClazz> list() throws JBException {
        final List<JBClazz>[] lists = new List[]{null};
        listFromJavabaas(true, new JBClazzListCallback() {
            @Override
            public void done(boolean success, List<JBClazz> list, JBException e) {
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

    public static void listInBackground(JBClazzListCallback callback) {
        listFromJavabaas(false, callback);
    }

    private static void listFromJavabaas(final boolean sync, final JBClazzListCallback callback) {
        String path = JBHttpClient.getClazzPath();
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, null, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                List<JBClazz> list = getClazzListFromMap(result.getData());
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

    public static JBClazzExport export(String className) throws JBException {
        final JBClazzExport[] lists = {null};
        exportFromJavabaas(true, className, new JBClazzExportCallback() {
            @Override
            public void done(boolean success, JBClazzExport clazzExport, JBException e) {
                if (success) {
                    lists[0] = clazzExport;
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

    public static void exportInBackground(String className, JBClazzExportCallback callback) {
        exportFromJavabaas(false, className, callback);
    }

    private static void exportFromJavabaas(final boolean sync, final String className, final JBClazzExportCallback callback) {
        String path = JBHttpClient.getClazzPath(className + "/" + "export");
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, null, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                    return;
                }
                if (result.getData() == null || result.getData().get("result") == null) {
                    callback.done(false, null,new JBException(JBCode.CLAZZ_NOT_FOUND));
                } else {
                    JBClazzExport clazzExport = getClazzExportFromMap((Map<String, Object>) result.getData().get("result"));
                    callback.done(true, clazzExport, null);
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
        importDataToJavabaas(true, data, new JBImportCallback() {
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

    public static void importDataInBackground(String data, JBImportCallback callback) {
        importDataToJavabaas(false, data, callback);
    }

    private static void importDataToJavabaas(final boolean sync, final String data, final JBImportCallback callback) {
        String path = JBHttpClient.getClazzPath("import");
        Map<String, Object> body = null;
        try {
            body = JBUtils.readValue(data, Map.class);
        } catch (JBException e) {
        }
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, null, body, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback != null) {
                    callback.done(true, null);
                }
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

    private static JBClazzExport getClazzExportFromMap(Map<String, Object> map) {
        try {
            String exportStr = JBUtils.writeValueAsString(map);
            JBClazzExport clazzExport = JBUtils.readValue(exportStr, JBClazzExport.class);
            return clazzExport;
        } catch (JBException e) {
            return null;
        }
    }

    private static List<JBClazz> getClazzListFromMap(LinkedHashMap<String, Object> map) {
        if (map == null || map.get("result") == null) {return new LinkedList<>();}
        List<Map<String, Object>> maps = (List<Map<String, Object>>) map.get("result");
        List<JBClazz> list = new LinkedList<>();
        for (Map<String, Object> o : maps) {
            list.add(copyClazzFromMap(o));
        }
        return list;
    }

    private static JBClazz copyClazzFromResultData(Map<String, Object> map) {
        if (map == null || map.get("result") == null) {return null;}
        return copyClazzFromMap((Map<String, Object>) map.get("result"));
    }

    private static JBClazz copyClazzFromMap(Map<String, Object> map) {
        if (map == null) {return null;}
        try {
            String clazzStr = JBUtils.writeValueAsString(map);
            JBClazz clazz = JBUtils.readValue(clazzStr, JBClazz.class);
            return clazz;
        } catch (JBException e) {
            return null;
        }

    }

    public static class JBClazzAcl extends LinkedHashMap<String, Object> {
        public JBClazzAcl() {
            super();
        }

        public void setPublicAccess(ClazzAclMethod method, boolean access) {
            Map<String, Boolean> accessMap = getAccessMap("*");
            accessMap.put(method.toString(), access);
            put("*", accessMap);
        }

        public void setAccess(ClazzAclMethod method, String userId, boolean access) {
            Map<String, Boolean> accessMap = getAccessMap(userId);
            accessMap.put(method.toString(), access);
            put(userId, accessMap);
        }

        public boolean hasAccess(ClazzAclMethod method, JBUser user) {
            if (user == null) {
                return hasPublicAccess(method);
            } else {
                return hasPublicAccess(method) || hasAccess(method, user.getObjectId());
            }
        }

        public boolean hasPublicAccess(ClazzAclMethod method) {
            return hasAccess(method, "*");
        }

        public boolean hasAccess(ClazzAclMethod method, String name) {
            Map<String, Boolean> map = getAccessMap(name);
            Boolean write = map.get(method.toString());
            if (write == null) {
                return false;
            } else {
                return write;
            }
        }

        private Map<String, Boolean> getAccessMap(String name) {
            Map<String, Boolean> accessMap = (Map<String, Boolean>) get(name);
            if (accessMap == null) {
                accessMap = new HashMap<>();
            }
            return accessMap;
        }

        @Override
        public String toString() {
            try {
                return JBUtils.writeValueAsString(this);
            } catch (JBException e) {
                return "";
            }
        }
    }

    public static enum ClazzAclMethod {

        INSERT("insert"),
        UPDATE("update"),
        GET("get"),
        FIND("find"),
        DELETE("delete");

        private String value;

        ClazzAclMethod(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

    }

    public static class JBClazzExport {
        private String id;
        private String name;
        private JBClazzAcl acl;
        private boolean internal;
        private List<JBField.JBFieldExport> fields;

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

        public JBClazzAcl getAcl() {
            return acl;
        }

        public void setAcl(JBClazzAcl acl) {
            this.acl = acl;
        }

        public boolean isInternal() {
            return internal;
        }

        public void setInternal(boolean internal) {
            this.internal = internal;
        }

        public List<JBField.JBFieldExport> getFields() {
            return fields;
        }

        public void setFields(List<JBField.JBFieldExport> fields) {
            this.fields = fields;
        }
    }

}
