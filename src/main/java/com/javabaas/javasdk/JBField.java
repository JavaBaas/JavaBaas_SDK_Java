package com.javabaas.javasdk;

import com.javabaas.javasdk.callback.*;

import java.util.*;

/**
 * Created by zangyilin on 2017/9/21.
 */
public class JBField {
    private String id;
    private JBClazz clazz;
    private String name;
    private int type;
    private boolean internal;//系统内建字段 禁止修改删除
    private boolean security;//安全字段 必须使用管理权限操作
    private boolean required;//必填字段

    public JBField() {
    }

    public JBField(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JBClazz getClazz() {
        return clazz;
    }

    public void setClazz(JBClazz clazz) {
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public boolean isSecurity() {
        return security;
    }

    public void setSecurity(boolean security) {
        this.security = security;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
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

    public void saveInBackground(JBSaveCallback callback) {
        saveToJavabaas(false, callback);
    }

    private void saveToJavabaas(final boolean sync, final JBSaveCallback callback) {
        if (getClazz() == null || JBUtils.isEmpty(getName())) {
            callback.done(false, new JBException(JBCode.REQUEST_PARAM_ERROR));
            return;
        }
        String className = getClazz().getName();
        String path = JBHttpClient.getFieldPath(className);
        Map<String, Object> body = getFieldMap();
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, null, body, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {return;}
                callback.done(true, null);
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
        deleteFieldFromJavabaas(true, new JBDeleteCallback() {
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
        deleteFieldFromJavabaas(false, callback);
    }

    private void deleteFieldFromJavabaas(final boolean sync, final JBDeleteCallback callback) {
        if (getClazz() == null || JBUtils.isEmpty(getName())) {
            callback.done(false, new JBException(JBCode.REQUEST_PARAM_ERROR.getCode(), "class或者field信息不能为空"));
            return;
        }
        String path = JBHttpClient.getFieldPath(getClazz().getName(), getName());
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.DELETE, null, null, sync, new JBObjectCallback() {
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

    public void update() throws JBException {
        updateFromJavabaas(true, new JBUpdateCallback() {
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

    public void updateInBackground(JBUpdateCallback callback) {
        updateFromJavabaas(false, callback);
    }

    private void updateFromJavabaas(final boolean sync, final JBUpdateCallback callback) {
        if (getClazz() == null || JBUtils.isEmpty(getName())) {
            callback.done(false, new JBException(JBCode.REQUEST_PARAM_ERROR.getCode(), "class或者field信息不能为空"));
            return;
        }
        String path = JBHttpClient.getFieldPath(getClazz().getName(), getName());
        Map<String, Object> body = getFieldMap();
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.PUT, null, body, sync, new JBObjectCallback() {
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

    public static JBField get(String className, String fieldName) throws JBException {
        final JBField[] fields = {null};
        getFieldFromJavabaas(className, fieldName, true, new JBGetFieldCallback() {
            @Override
            public void done(boolean success, JBField field, JBException e) {
                if (success) {
                    fields[0] = field;
                } else {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return fields[0];
    }

    public static void getInBackground(String className, String fieldName, JBGetFieldCallback callback) {
        getFieldFromJavabaas(className, fieldName, false, callback);
    }

    private static void getFieldFromJavabaas(final String className, final String fieldName, final boolean sync, final JBGetFieldCallback callback) {
        if (JBUtils.isEmpty(className) || JBUtils.isEmpty(fieldName)) {
            callback.done(false, null, new JBException(JBCode.REQUEST_PARAM_ERROR));
            return;
        }
        String path = JBHttpClient.getFieldPath(className, fieldName);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, null, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                JBField field =  copyFieldFromResultData(result.getData());
                if (callback != null) {
                    callback.done(true, field, null);
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

    public static List<JBField> list(String className) throws JBException {
        final List<JBField>[] lists = new List[]{null};
        listFromJavabaas(className, true, new JBFieldListCallback() {
            @Override
            public void done(boolean success, List<JBField> list, JBException e) {
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

    public static void listInBackground(String className, JBFieldListCallback callback) {
        listFromJavabaas(className, false, callback);
    }

    private static void listFromJavabaas(final String className, final boolean sync, final JBFieldListCallback callback) {
        String path = JBHttpClient.getFieldPath(className);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, null, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                List<JBField> list = getFieldListFromMap(result.getData());
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

    private static List<JBField> getFieldListFromMap(LinkedHashMap<String, Object> map) {
        if (map == null || map.get("result") == null) {return new LinkedList<>();}
        List<Map<String, Object>> maps = (List<Map<String, Object>>) map.get("result");
        List<JBField> list = new LinkedList<>();
        for (Map<String, Object> objectMap : maps) {
            list.add(copyFieldFromMap(objectMap));
        }
        return list;
    }

    private static JBField copyFieldFromResultData(Map<String, Object> map) {
        if (map == null || map.get("result") == null) {return null;}
        return copyFieldFromMap((Map<String, Object>) map.get("result"));
    }

    private static JBField copyFieldFromMap(Map<String, Object> map) {
        if (map == null) {return null;}
        String FieldStr = JBUtils.writeValueAsString(map);
        JBField field = JBUtils.readValue(FieldStr, JBField.class);
        return field;
    }

    private Map<String,Object> getFieldMap() {
        Map<String, Object> map = new HashMap<>();
        if (!JBUtils.isEmpty(getId())) {
            map.put("id", getId());
        }
        if (!JBUtils.isEmpty(getName())) {
            map.put("name", getName());
        }
        if (getClazz() != null) {
            map.put("clazz", getClazz());
        }
        map.put("type", getType());
        map.put("internal", isInternal());
        map.put("security", isSecurity());
        map.put("required", isRequired());
        return map;
    }

    public static class JBFieldExport {
        private String id;
        private String name;
        private int type;
        private boolean internal;
        private boolean security;
        private boolean required;

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

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public boolean isInternal() {
            return internal;
        }

        public void setInternal(boolean internal) {
            this.internal = internal;
        }

        public boolean isSecurity() {
            return security;
        }

        public void setSecurity(boolean security) {
            this.security = security;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }
    }
}
