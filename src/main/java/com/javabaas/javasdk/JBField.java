package com.javabaas.javasdk;

import com.javabaas.javasdk.callback.JBBooleanCallback;
import com.javabaas.javasdk.callback.JBFieldListCallback;
import com.javabaas.javasdk.callback.JBGetFieldCallback;
import com.javabaas.javasdk.callback.JBObjectCallback;

import java.util.*;

/**
 * Created by zangyilin on 2017/9/21.
 */
public class JBField {
    private String id;
    private JBClazz clazz;
    private String name;
    private int type;
    /**
     * 系统内建字段 禁止修改删除
     */
    private boolean internal;
    /**
     * 安全字段 必须使用管理权限操作
     */
    private boolean security;
    /**
     * 必填字段
     */
    private boolean notnull;
    /**
     * 只读字段
     */
    private boolean readonly;

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

    public boolean isNotnull() {
        return notnull;
    }

    public void setNotnull(boolean notnull) {
        this.notnull = notnull;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    /**
     * 创建字段 同步
     *
     * @throws JBException 异常信息
     */
    public void save() throws JBException {
        saveToJavabaas(true, new JBBooleanCallback() {
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

    /**
     * 创建字段 异步
     *
     * @param callback 成功或失败回调
     */
    public void saveInBackground(JBBooleanCallback callback) {
        saveToJavabaas(false, callback);
    }

    private void saveToJavabaas(final boolean sync, final JBBooleanCallback callback) {
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

    /**
     * 删除字段 同步
     *
     * @throws JBException 异常信息
     */
    public void delete() throws JBException {
        deleteFieldFromJavabaas(true, new JBBooleanCallback() {
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

    /**
     * 删除字段 异步
     *
     * @param callback 删除字段结果回调
     */
    public void deleteInBackground(JBBooleanCallback callback) {
        deleteFieldFromJavabaas(false, callback);
    }

    private void deleteFieldFromJavabaas(final boolean sync, final JBBooleanCallback callback) {
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

    /**
     * 更新字段信息 同步<br/>
     * 目前更新字段信息只能更新字段security和notnull信息
     *
     * @throws JBException 异常信息
     */
    public void update() throws JBException {
        updateFromJavabaas(true, new JBBooleanCallback() {
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

    /**
     * 更新字段信息 异步<br/>
     * 目前更新字段信息只能更新字段security和notnull信息
     *
     * @param callback
     */
    public void updateInBackground(JBBooleanCallback callback) {
        updateFromJavabaas(false, callback);
    }

    private void updateFromJavabaas(final boolean sync, final JBBooleanCallback callback) {
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

    /**
     * 获取字段信息 同步
     *
     * @param className    类名
     * @param fieldName    字段名
     * @return             字段信息
     * @throws JBException 异常信息
     */
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

    /**
     * 获取字段信息 异步
     *
     * @param className    类名
     * @param fieldName    字段名
     * @param callback     字段信息回调
     */
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

    /**
     * 查看类中所有字段信息 同步
     *
     * @param className     类名
     * @return              字段信息列表
     * @throws JBException  异常信息
     */
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

    /**
     * 查看类中所有字段信息 异步
     *
     * @param className     类名
     * @param callback      字段信息集合回调
     */
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
        try {
            String FieldStr = JBUtils.writeValueAsString(map);
            JBField field = JBUtils.readValue(FieldStr, JBField.class);
            return field;
        } catch (JBException e) {
            return null;
        }
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
        map.put("notnull", isNotnull());
        map.put("readonly", isReadonly());
        return map;
    }

    public static class JBFieldExport {
        private String id;
        private String name;
        private int type;
        private boolean internal;
        private boolean security;
        private boolean notnull;
        private boolean readonly;

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

        public boolean isNotnull() {
            return notnull;
        }

        public void setNotnull(boolean notnull) {
            this.notnull = notnull;
        }

        public boolean isReadonly() {
            return readonly;
        }

        public void setReadonly(boolean readonly) {
            this.readonly = readonly;
        }
    }
}
