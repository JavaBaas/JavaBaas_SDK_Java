package com.javabaas.javasdk;


import com.javabaas.javasdk.callback.*;

import java.util.*;

/**
 * Created by zangyilin on 2017/8/9.
 */
public class JBQuery<T extends JBObject> {
    private String className;
    private String whereSting;
    private Boolean isRunning;
    JBQueryConditions conditions;

    private JBQuery() {
        super();
    }

    public String getClassName() {
        return className;
    }

    public JBQuery<T> setClassName(String className) {
        this.className = className;
        return this;
    }

    public Boolean getRunning() {
        return isRunning;
    }

    public void setRunning(Boolean running) {
        isRunning = running;
    }

    public List<String> getInclude() {
        return conditions.getInclude();
    }

    public void setInclude(List<String> include) {
        conditions.setInclude(include);
    }

    public Set<String> getSelectedKeys() {
        return conditions.getSelectedKeys();
    }

    public void setSelectedKeys(Set<String> selectedKeys) {
        conditions.setSelectedKeys(selectedKeys);
    }

    // 这个只是针对传递过来json字符串，对于JBQueryConditions的where不适用
    public String getWhereSting() {
        return whereSting;
    }

    // 这个会把其他where条件全部替换
    public void setWhereSting(String whereSting) {
        this.whereSting = whereSting;
    }

    public Map<String, String> getParameters() {
        Map<String, String> map = conditions.getParameters();
        if (!JBUtils.isEmpty(whereSting)) {
            map.put(JBQueryConditions.WHERE, whereSting);
        }
        return map;
    }

    public void setParameters(Map<String, String> parameters) {
        conditions.setParameters(parameters);
    }

    public Map<String, List<JBQueryOperation>> getWhere() {
        return conditions.getWhere();
    }

    public JBQuery(String className) {
        JBUtils.checkClassName(className);
        this.className = className;
        this.conditions = new JBQueryConditions();
    }

    public static JBQuery createQuery(JBObject object) {
        if (!JBUtils.isEmpty(object.getClassName())) {
            return new JBQuery(object.getClassName());
        } else {
            return null;
        }
    }

    public static <T extends JBObject> JBQuery<T> getQuery(String className) {
        return new JBQuery<>(className);
    }


    public int getLimit() {
        return conditions.getLimit();
    }

    public JBQuery<T> setLimit(int limit) {
        conditions.setLimit(limit);
        return this;
    }

    public JBQuery<T> limit(int limit) {
        setLimit(limit);
        return this;
    }

    public int getSkip() {
        return conditions.getSkip();
    }

    public JBQuery<T> setSkip(int skip) {
        conditions.setSkip(skip);
        return this;
    }

    public JBQuery<T> skip(int skip) {
        setSkip(skip);
        return this;
    }

    public String getOrder() {
        return conditions.getOrder();
    }

    public JBQuery<T> setOrder(String order) {
        conditions.setOrder(order);
        return this;
    }

    public JBQuery<T> order(String order) {
        setOrder(order);
        return this;
    }

    public JBQuery<T> include(String key) {
        conditions.include(key);
        return this;
    }

    public JBQuery<T> addAscendingOrder(String key) {
        conditions.addAscendingOrder(key);
        return this;
    }

    public JBQuery<T> addDescendingOrder(String key) {
        conditions.addDescendingOrder(key);
        return this;
    }

    public JBQuery<T> selectKeys(Collection<String> keys) {
        conditions.selectKeys(keys);
        return this;
    }

    public JBQuery<T> orderByAscending(String key) {
        conditions.orderByAscending(key);
        return this;
    }

    public JBQuery<T> orderByDescending(String key) {
        conditions.orderByDescending(key);
        return this;
    }

    public JBQuery<T> whereExists(String key) {
        conditions.whereExists(key);
        return this;
    }

    public JBQuery<T> whereNotExist(String key) {
        conditions.whereNotExist(key);
        return this;
    }

    public JBQuery<T> whereEqualTo(String key, Object value) {
        conditions.whereEqualTo(key, value);
        return this;
    }

    public JBQuery<T> whereNotEqualTo(String key, Object value) {
        conditions.whereNotEqualTo(key, value);
        return this;
    }

    public JBQuery<T> whereGreaterThanOrEqualTo(String key, Object value) {
        conditions.whereGreaterThanOrEqualTo(key, value);
        return this;
    }

    public JBQuery<T> whereGreaterThan(String key, Object value) {
        conditions.whereGreaterThan(key, value);
        return this;
    }

    public JBQuery<T> whereLessThan(String key, Object value) {
        conditions.whereLessThan(key, value);
        return this;
    }

    public JBQuery<T> whereLessThanOrEqualTo(String key, Object value) {
        conditions.whereLessThanOrEqualTo(key, value);
        return this;
    }

    public JBQuery<T> whereContains(String key, String substring) {
        conditions.whereContains(key, substring);
        return this;
    }

    public JBQuery<T> whereContainedIn(String key, Collection<? extends Object> values) {
        conditions.whereContainedIn(key, values);
        return this;
    }

    public JBQuery<T> whereNotContainedIn(String key, Collection<? extends Object> values) {
        conditions.whereNotContainedIn(key, values);
        return this;
    }

    public JBQuery<T> whereStartWith(String key, String prefix) {
        conditions.whereStartWith(key, prefix);
        return this;
    }

    public JBQuery<T> whereEndWith(String key, String suffix) {
        conditions.whereEndWith(key, suffix);
        return this;
    }

    public JBQuery<T> whereMatches(String key, String regex) {
        conditions.whereMatches(key, regex);
        return this;
    }

    public JBQuery<T> whereMatches(String key, String regex, String modifiers) {
        conditions.whereMatches(key, regex, modifiers);
        return this;
    }

    private JBQuery<T> addWhereItem(JBQueryOperation op) {
        conditions.addWhereItem(op);
        return this;
    }

    private JBQuery<T> addOrItems(JBQueryOperation op) {
        conditions.addOrItems(op);
        return this;
    }

    private JBQuery<T> addAndItems(JBQuery query) {
        conditions.addAndItems(query.conditions);
        return this;
    }

    protected JBQuery<T> addWhereItem(String key, String op, Object value) {
        conditions.addWhereItem(key, op, value);
        return this;
    }

    public JBQuery<T> whereMatchesQuery(String key, JBQuery<?> query) {
        return whereMatchesKeyInQuery(key, null, query, null);
    }

    public JBQuery<T> whereMatchesKeyInQuery(String key, String searchKey, JBQuery<?> query, String targetClass) {
        Map<String, Object> map = new HashMap<>();
        map.put("searchClass", query.getClassName());
        map.put(JBQueryConditions.WHERE, query.conditions.compileWhereOperationMap());
        if (!JBUtils.isEmpty(searchKey)) {
            map.put("searchKey", searchKey);
        }
        if (!JBUtils.isEmpty(targetClass)) {
            map.put("targetClass", targetClass);
        }
        Map<String, Object> sub = new HashMap<>();
        sub.put("$sub", map);
        conditions.addWhereItem(key, JBQueryOperation.EQUAL_OP, sub);
        return this;
    }

    //todo 缺少子查询

    public T get(String objectId) throws JBException {
        if (JBUtils.isEmpty(objectId) ) {
            throw new JBException(JBCode.REQUEST_PARAM_ERROR);
        } else {
            final Object[] result = {null};
            getFromJavaBaas(objectId, true, new JBGetCallback<T>() {
                @Override
                public void done(boolean success, T object, JBException e) {
                    if (success) {
                        result[0] = object;
                    } else {
                        JBExceptionHolder.add(e);
                    }
                }
            });
            if (JBExceptionHolder.exists()) {
                throw JBExceptionHolder.remove();
            }
            return (T) result[0];
        }
    }

    public void getInBackground(String objectId, JBGetCallback callback) {
        getFromJavaBaas(objectId, false, callback);
    }

    private void getFromJavaBaas(final String objectId, final boolean sync, final JBGetCallback<T> callback) {
        if (JBUtils.isEmpty(objectId)) {
            callback.done(false, null, new JBException(JBCode.REQUEST_PARAM_ERROR));
            return;
        }
        assembleParameters();
        String path = JBHttpClient.getObjectPath(this.className, objectId);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, new JBHttpParams(getParameters()), null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                    return;
                }
                List<T> list = processResults(result.getData());
                if (list.size() > 0) {
                    T o = list.get(0);
                    callback.done(true, o, null);
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

    public List<T> find() throws JBException {
        assembleParameters();
        String path = JBHttpClient.getObjectPath(this.className);
        final List<T>[] result = new List[]{null};
        findFromJavaBaas(true, new JBFindCallBack() {
            @Override
            public void done(boolean success, List objects, JBException e) {
                if (success) {
                    result[0] = objects;
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

    public void findInBackground(JBFindCallBack<T> callBack) {
        findFromJavaBaas(false, callBack);
    }

    private void findFromJavaBaas(final boolean sync, final JBFindCallBack callback) {
        assembleParameters();
        String path = JBHttpClient.getObjectPath(this.className);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, new JBHttpParams(getParameters()), null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                List<T> list = processResults(result.getData());
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

    public int count() throws JBException {
        final int[] value = {0};
        countFromJavabaas(true, new JBCountCallback() {
            @Override
            public void done(boolean success, int count, JBException e) {
                if (success) {
                    value[0] = count;
                } else {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return value[0];
    }

    public void countInBackground(JBCountCallback callback) {
        countFromJavabaas(false, callback);
    }

    private void countFromJavabaas(final boolean sync, final JBCountCallback callback) {
        assembleParameters();
        String path = JBHttpClient.getObjectPath(this.className, "count");
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, new JBHttpParams(getParameters()), null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback ==  null) {
                    return;
                }
                try {
                    int count = (int) result.getData().get("count");
                    callback.done(true, count, null);
                } catch (Exception e) {
                    callback.done(false, 0, new JBException(JBCode.INTERNAL_JSON_ERROR));
                }
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, 0, error);
                }
            }
        });
    }

    public void deleteByQuery() throws Exception {
        deleteByQueryFromJavabaas(true, new JBDeleteCallback() {
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

    public void deleteByQueryInBackground() {
        deleteByQueryFromJavabaas(false, null);
    }

    public void deleteByQueryInBackground(JBDeleteCallback callback) {
        deleteByQueryFromJavabaas(false, callback);
    }

    private void deleteByQueryFromJavabaas(final boolean sync, final JBDeleteCallback callback) {
        assembleParameters();
        String path = JBHttpClient.getObjectPath(this.className, "deleteByQuery");
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.DELETE, new JBHttpParams(getParameters()), null, sync, new JBObjectCallback() {
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


    protected Map<String, String> assembleParameters() {
        return conditions.assembleParameters();
    }

    protected List<T> processResults(Map<String, Object> map) {
        if (map == null || map.get("result") == null) {
            return Collections.emptyList();
        }
        List<T> result = new LinkedList<>();
        List<Map<String, Object>> list;
        Object o = map.get("result");
        if (o instanceof Map) {
            list = new ArrayList<>();
            list.add((Map<String, Object>) o);
        } else if (o instanceof Collection) {
            list = (List) map.get("result");
        } else {
            return Collections.emptyList();
        }
        for (Map<String, Object> object : list) {
            JBObject jbObject = new JBObject(className);
            JBUtils.copyPropertiesFromMapToJBObject(jbObject, object);
            result.add((T) jbObject);
        }
        return result;
    }
}
