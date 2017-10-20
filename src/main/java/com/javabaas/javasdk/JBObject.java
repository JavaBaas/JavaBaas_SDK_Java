package com.javabaas.javasdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.javabaas.javasdk.callback.JBDeleteCallback;
import com.javabaas.javasdk.callback.JBObjectCallback;
import com.javabaas.javasdk.callback.JBSaveCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zangyilin on 2017/8/9.
 */
public class JBObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(JBObject.class);

    public static final String CREATED_AT = "createdAt";
    public static final String UPDATED_AT = "updatedAt";
    public static final String OBJECT_ID = "_id";

    ReadWriteLock lock = new ReentrantReadWriteLock();

    private volatile boolean fetchWhenSave = false;

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

    transient protected JBAcl acl;

    Map<String, Object> serverData;
    Map<String, JBOperator> operationQueue;

    public Map<String, Object> getServerData() {
        return serverData;
    }

    public void setServerData(Map<String, Object> serverData) {
        this.serverData = serverData;
    }

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
            LOGGER.info(className + "-> e: " + e);
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
        INVALID_KEYS.add("uuid");
        INVALID_KEYS.add("className");
        INVALID_KEYS.add("keyValues");
        INVALID_KEYS.add("fetchWhenSave");
        INVALID_KEYS.add("running");
        INVALID_KEYS.add("acl");
        INVALID_KEYS.add("ACL");
        INVALID_KEYS.add("isDataReady");
        INVALID_KEYS.add("pendingKeys");
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
        operationQueue = new HashMap<>();
    }

    public JBObject(String theClassName) {
        this();
        JBUtils.checkClassName(theClassName);
        className = theClassName;
    }

    public void put(final String key, final Object value) {
        try {
            lock.writeLock().lock();
            if (checkKey(key)) {
                serverData.put(key, value);
            }
        } catch (Exception e) {
            LOGGER.info(className + "-> e: " + e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean checkKey(String key) {
        if (JBUtils.isEmpty(key))
            throw new IllegalArgumentException("字段为空");
        if (key.startsWith("_")) {
            throw new IllegalArgumentException("字段不能以'_'开头");
        }
        if (INVALID_KEYS.contains(key)) {
            LOGGER.info(className + "-> " + key + " 内部key");
            return false;
        }
        return true;
    }

    public Date getDate(String key) {
        return JBUtils.dateFromString((String) get(key));
    }

    public void removeKey(String key) {
        try {
            lock.writeLock().lock();
            if (!checkKey(key)) {
                return;
            }
            operationQueue.put(key, new JBOperator(JBOperatorType.DELETE));
        } catch (Exception e) {
            LOGGER.info(className + "-> e: " + e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void increment(String key) {
        increment(key, 1);
    }

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
            LOGGER.info(className + "-> e: " + e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addArray(String key, Object object) {
        List<Object> list = new ArrayList<>();
        list.add(object);
        addArray(key, list);
    }

    public void addArray(String key, Collection<?> list) {
        controlObjectToArray(key, list, JBOperatorType.ADD);
    }

    /**
     * 只能保证不会增加和之前重复的值，不能对之前已经存在的数据去重
     * @param key 字段
     * @param list 数组
     */
    public void addUniqueArray(String key, Collection<?> list) {
        controlObjectToArray(key, list, JBOperatorType.ADDUNIQUE);
    }

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
            LOGGER.info(className + "-> e: " + e);
        } finally {
            lock.writeLock().unlock();
        }
    }

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
            LOGGER.info(className + "-> e: " + e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static JBObject create(String className) {
        return new JBObject(className);
    }

    public static JBObject createWithOutData(String className, String objectId) {
        JBObject jbObject = new JBObject(className);
        if (JBUtils.isEmpty(objectId)) {
            throw new IllegalArgumentException("objectId 为空");
        }
        jbObject.setObjectId(objectId);
        return jbObject;
    }

    public void save() throws JBException {
        saveObjectToJavaBaas(true, new JBSaveCallback() {
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

    public void saveInBackground() {
        saveObjectToJavaBaas(false, null);
    }

    public void saveInBackground(JBSaveCallback callback) {
        this.saveObjectToJavaBaas(false, callback);
    }

    private void saveObjectToJavaBaas(final boolean sync, final JBSaveCallback callback) {
        String urlPath = JBHttpClient.getObjectPath(this.className, this.objectId);
        JBHttpMethod method;
        if (!JBUtils.isEmpty(this.objectId)) {
            method = JBHttpMethod.PUT;
        } else {
            method = JBHttpMethod.POST;
        }
        JBHttpParams jbHttpParams = new JBHttpParams();
        jbHttpParams.put("fetch", this.fetchWhenSave);
        Map<String, Object> body = getObjectBody();
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

    protected Map<String, Object> getObjectBody() {
        Map<String, Object> body = new HashMap<>();
        if (this.acl != null) {
            body.putAll(acl.getAclMap());
        }
        if (this.serverData.size() > 0) {
            body.putAll(this.serverData);
        }
        if (this.operationQueue.size() > 0) {
            body.putAll(this.operationQueue);
        }
        return body;
    }

    public void delete() throws JBException {
        deleteFromJavabaas(true, new JBDeleteCallback() {
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

    public void deleteInBackground() {
        deleteFromJavabaas(false, null);
    }

    public void deleteInBackground(JBDeleteCallback callback) {
        deleteFromJavabaas(false, callback);
    }

    private void deleteFromJavabaas(final boolean sync, final JBDeleteCallback callback) {
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

    public Map<String, Object> getPointer() {
        if (JBUtils.isEmpty(className) || JBUtils.isEmpty(objectId)) {
            return null;
        }
        return new JBPointer(className, objectId);
    }


    @Override
    public String toString() {
        return "{" +
                "\"className\":\"" + className + "\"" +
                ", \"objectId\":\"" + objectId + "\"" +
                ", \"updatedAt\":\"" + updatedAt + "\"" +
                ", \"createdAt\":\"" + createdAt + "\"" +
                ", \"serverData\":" + JBUtils.writeValueAsString(serverData) +
                '}';
    }
}
