package com.javabaas.javasdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.javabaas.javasdk.callback.JBBooleanCallback;
import com.javabaas.javasdk.callback.JBObjectCallback;
import com.javabaas.javasdk.log.JBLogUtil;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zangyilin on 2017/8/9.
 */
public class JBObject {


    public static final String CREATED_AT = "createdAt";
    public static final String UPDATED_AT = "updatedAt";
    public static final String OBJECT_ID = "_id";

    ReadWriteLock lock = new ReentrantReadWriteLock();

    private volatile boolean fetchWhenSave = false;
    // 按条件查询或更新对象时候的查询条件
    private JBQuery query;
    // 类名
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String className;
    // 文档Id
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String objectId;
    // 更新时间
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String updatedAt;
    // 创建时间
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String createdAt;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setQuery(JBQuery query) {
        this.query = query;
    }

    transient protected JBAcl acl;

    Map<String, Object> serverData;
    Map<String, Object> saveData;
    Map<String, JBOperator> operationQueue;

    public Map<String, Object> getServerData() {
        return serverData;
    }

    public void setServerData(Map<String, Object> serverData) {
        this.serverData = serverData;
    }

    /**
     * 为true时会在保存后返回对象最新值<br/>
     * 如果在创建或更新对象时希望返回对象最新值可以使用本setFetchWhenSave
     */
    public void setFetchWhenSave(boolean fetchWhenSave) {
        this.fetchWhenSave = fetchWhenSave;
    }

    public Object get(String key) {
        if (CREATED_AT.equals(key)) {
            return getCreatedAt();
        }
        if (UPDATED_AT.equals(key)) {
            return getUpdatedAt();
        }
        Object value = null;
        try {
            lock.readLock().lock();
            value = serverData.get(key);
        } catch (Exception e) {
            JBLogUtil.log.e("获取失败", e);
        } finally {
            lock.readLock().unlock();
        }
        return value;
    }

    public JBAcl getAcl() {
        return acl;
    }

    public void setAcl(JBAcl acl) {
        this.acl = acl;
    }

    public static final Set<String> INVALID_KEYS = new HashSet<>();

    static {
        INVALID_KEYS.add("code");
        INVALID_KEYS.add("className");
        INVALID_KEYS.add("fetchWhenSave");
        INVALID_KEYS.add("running");
        INVALID_KEYS.add("acl");
        INVALID_KEYS.add("ACL");
        INVALID_KEYS.add(CREATED_AT);
        INVALID_KEYS.add(UPDATED_AT);
        INVALID_KEYS.add(OBJECT_ID);
        INVALID_KEYS.add("createdPlat");
        INVALID_KEYS.add("updatedPlat");
        INVALID_KEYS.add("error");
        INVALID_KEYS.add("searchClass");
        INVALID_KEYS.add("searchKey");
        INVALID_KEYS.add("targetClass");
    }

    public JBObject() {
        objectId = "";
        serverData = new HashMap<>();
        saveData = new HashMap<>();
        operationQueue = new HashMap<>();
    }

    public JBObject(String theClassName) {
        this();
        JBUtils.checkClassName(theClassName);
        className = theClassName;
    }

    /**
     * 添加字段值
     *
     * @param key 字段
     * @param value 值
     */
    public void put(final String key, final Object value) {
        try {
            lock.writeLock().lock();
            if (checkKey(key)) {
                serverData.put(key, value);
                updateSaveData(key, value);
            }
        } catch (Exception e) {
            JBLogUtil.log.w("数据处理错误");
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void updateSaveData(String key, Object value) {
        if (value == null) {
            saveData.remove(key);
        } else if (value instanceof JBFile || value instanceof JBUser || value instanceof JBObject) {
            Map<String, Object> map = new HashMap<>();
            map.put("__type", value instanceof JBFile ? "File" : "Pointer");
            map.put("className", ((JBObject)value).getClassName());
            map.put("_id", ((JBObject)value).getObjectId());
            saveData.put(key, map);
        } else {
            saveData.put(key, value);
        }
    }

    private boolean checkKey(String key) {
        if (JBUtils.isEmpty(key))
            throw new IllegalArgumentException("字段为空");
        if (key.startsWith("_")) {
            throw new IllegalArgumentException("字段不能以'_'开头");
        }
        if (INVALID_KEYS.contains(key)) {
            return false;
        }
        return true;
    }

    public Date getDate(String key) {
        return JBUtils.dateFromString((String) get(key));
    }

    /**
     * 原子操作 删除删除字段值
     *
     * @param key 待操作的字段
     */
    public void removeKey(String key) {
        try {
            lock.writeLock().lock();
            if (!checkKey(key)) {
                return;
            }
            operationQueue.put(key, new JBOperator(JBOperatorType.DELETE));
        } catch (Exception e) {
            JBLogUtil.log.w("数据处理错误");
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 原子操作 Number类型字段值加1
     *
     * @param key 待操作的字段
     */
    public void increment(String key) {
        increment(key, 1);
    }

    /**
     * 原子操作 Number类型字段值加给的增量值
     *
     * @param key 待操作的字段
     * @param amount 增量值
     */
    public void increment(final String key, final Number amount) {
        try {
            lock.writeLock().lock();
            if (!checkKey(key)) {
                return;
            }
            JBOperator operator = new JBOperator(JBOperatorType.INCREMENT);
            operator.setAmount(amount.longValue());
            operationQueue.put(key, operator);
        } catch (Exception e) {
            JBLogUtil.log.w("数据处理错误");
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 原子操作 Array类型字段添加单个值
     *
     * @param key 待操作的字段
     * @param object 待添加对象
     */
    public void addArray(String key, Object object) {
        List<Object> list = new ArrayList<>();
        list.add(object);
        addArray(key, list);
    }

    /**
     * 原子操作 Array类型字段添加list值
     *
     * @param key 待操作的字段
     * @param list 待添加list
     */
    public void addArray(String key, Collection<?> list) {
        controlObjectToArray(key, list, JBOperatorType.ADD);
    }

    /**
     * 原子操作 Array类型字段添加与之前不重复的值<br/>
     * 只能保证不会增加和之前重复的值，不能对之前已经存在的数据去重
     *
     * @param key 待操作的字段
     * @param list 待操作数组
     */
    public void addUniqueArray(String key, Collection<?> list) {
        controlObjectToArray(key, list, JBOperatorType.ADDUNIQUE);
    }

    /**
     * 原子操作 Array类型字段删除值
     *
     * @param key 待操作的字段
     * @param list 待操作数组
     */
    public void removeArray(String key, Collection<?> list) {
        controlObjectToArray(key, list, JBOperatorType.REMOVE);
    }

    private void controlObjectToArray(final String key, final Collection<?> objects, final JBOperatorType operatorType) {
        try {
            lock.writeLock().lock();
            if (!checkKey(key)) {
                return;
            }
            JBOperator operator = operationQueue.get(key);
            if (operator == null || (operator != null && operator.getOp() != operatorType)) {
                operator = new JBOperator(operatorType);
            }
            List<Object> list = operator.getObjects();
            for (Object o : objects) {
                list.add(o);
            }
            operator.setObjects(list);
            operationQueue.put(key, operator);
        } catch (Exception e) {
            JBLogUtil.log.w("数据处理错误");
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 原子操作 Number类型字段原子倍数增加
     *
     * @param key 待操作的字段
     * @param amount 待增加的倍数值
     */
    public void multiply(final String key, final Number amount) {
        try {
            lock.writeLock().lock();
            if (!checkKey(key)) {
                return;
            }
            JBOperator operator = new JBOperator(JBOperatorType.MULTIPLY);
            operator.setAmount(amount.longValue());
            operationQueue.put(key, operator);
        } catch (Exception e) {
            JBLogUtil.log.w("数据处理错误");
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 创建JBObject对象
     *
     * @param className 类名
     * @return JBObject
     */
    public static JBObject create(String className) {
        return new JBObject(className);
    }

    /**
     * 创建一个只含有objectId的引用对象
     *
     * @param className 类名
     * @param objectId 对象id
     * @return
     */
    public static JBObject createWithoutData(String className, String objectId) {
        JBObject jbObject = new JBObject(className);
        if (JBUtils.isEmpty(objectId)) {
            throw new IllegalArgumentException("objectId 为空");
        }
        jbObject.setObjectId(objectId);
        return jbObject;
    }

    /**
     * 新建或保存对象 同步<br/>
     * 如果对象中objectId不为空，则视为更新操作，如果objectId为空，则视为新建操作
     *
     * @throws JBException 异常
     */
    public void save() throws JBException {
        saveObjectToJavaBaas(true, new JBBooleanCallback() {
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
     * 新建或保存对象 异步 无回调<br/>
     * 如果对象中objectId不为空，则视为更新操作，如果objectId为空，则视为新建操作
     *
     */
    public void saveInBackground() {
        saveObjectToJavaBaas(false, null);
    }

    /**
     * 新建或保存对象 异步 有回调<br/>
     * 如果对象中objectId不为空，则视为更新操作，如果objectId为空，则视为新建操作
     *
     * @param callback 操作成功或失败的回调
     */
    public void saveInBackground(JBBooleanCallback callback) {
        this.saveObjectToJavaBaas(false, callback);
    }

    private void saveObjectToJavaBaas(final boolean sync, final JBBooleanCallback callback) {
        String urlPath = JBHttpClient.getObjectPath(this.className, this.objectId);
        JBHttpMethod method;
        if (!JBUtils.isEmpty(this.objectId)) {
            method = JBHttpMethod.PUT;
        } else {
            method = JBHttpMethod.POST;
        }
        JBHttpParams jbHttpParams = new JBHttpParams();
        jbHttpParams.put("fetch", this.fetchWhenSave);
        if (this.query != null && this.query.getWhere() != null) {
            this.query.assembleParameters();
            try {
                jbHttpParams.put("where", JBUtils.writeValueAsString(this.query.getWhere()));
            } catch (JBException e) {
                if (callback != null) {
                    callback.done(false, e);
                }
            }
        }
        Map<String, Object> body = getObjectForSaveBody();
        JBHttpClient.INSTANCE().sendRequest(urlPath, method, jbHttpParams, body, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (result.getData() != null && result.getData().get("result") != null) {
                    copyFromMap((Map<String, Object>) result.getData().get("result"));
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

    protected Map<String, Object> getObjectForSaveBody() {
        Map<String, Object> body = new HashMap<>();
        if (this.acl != null) {
            body.putAll(acl.getAclMap());
        }
        if (this.saveData.size() > 0) {
            body.putAll(this.saveData);
        }
        if (this.operationQueue.size() > 0) {
            body.putAll(this.operationQueue);
        }
        return body;
    }

    /**
     * 删除对象 同步
     *
     * @throws JBException 异常信息
     */
    public void delete() throws JBException {
        deleteFromJavabaas(true, new JBBooleanCallback() {
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
     * 删除对象 异步 无回调
     */
    public void deleteInBackground() {
        deleteFromJavabaas(false, null);
    }

    /**
     * 删除对象 异步 有回调
     *
     * @param callback 成功或失败回调
     */
    public void deleteInBackground(JBBooleanCallback callback) {
        deleteFromJavabaas(false, callback);
    }

    private void deleteFromJavabaas(final boolean sync, final JBBooleanCallback callback) {
        if (JBUtils.isEmpty(this.objectId)) {
            callback.done(false, new JBException(JBCode.OBJECT_ID_ERROR));
            return;
        }
        String path = JBHttpClient.getObjectPath(this.className, this.objectId);
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
                if (callback != null) {
                    callback.done(false, error);
                }
            }
        });

    }


    protected void copyFromMap(Map<String, Object> map) {
        try {
            lock.writeLock().lock();
            JBUtils.copyPropertiesFromMapToJBObject(this, map);
        } catch (Exception e) {
            throw new IllegalArgumentException("Map2JBObject 错误");
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String toString() {
        String serverDataStr = "";
        try {
            serverDataStr = JBUtils.writeValueAsString(serverData);
        } catch (JBException e) { }

        return "{" +
                "\"className\":\"" + className + "\"" +
                ", \"objectId\":\"" + objectId + "\"" +
                ", \"updatedAt\":\"" + updatedAt + "\"" +
                ", \"createdAt\":\"" + createdAt + "\"" +
                ", \"serverData\":" + serverDataStr +
                '}';

    }
}
