package com.javabaas.javasdk;


import com.javabaas.javasdk.callback.*;

import java.util.*;

/**
 * 查询相关
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

    /**
     * 获取当前query对应的主体类类名
     *
     * @return 类名
     */
    public String getClassName() {
        return className;
    }

    public JBQuery<T> setClassName(String className) {
        this.className = className;
        return this;
    }

    /**
     * 查询当前查询是否进行中
     *
     * @return 结果
     */
    public Boolean getRunning() {
        return isRunning;
    }

    public void setRunning(Boolean running) {
        isRunning = running;
    }

    public List<String> getInclude() {
        return conditions.getInclude();
    }

    /**
     * 添加include的内容<br/>
     * <br/>
     *
     * 如果查询的字段是Pointer类型,通过设置include可以将Pointer字段的详细信息查询出来.<br/>
     * 例如有个字段为sound,音频,是Pointer类型,对应的class是Sound,则查询该字段的详细信息可以添加"sound"到include中.<br/>
     * 如果Sound还有有个字段为user,是Pointer类型,对应的class是_User,<br/>
     * 则查询sound字段对应的详细信息中的user字段的详细信息,可以添加"sound.user"到include中.<br/><br/>
     *
     * pointer字段的层级通过"."来分隔,例如"a.b.c",表示当前查询的主体类中有Pointer字段a,a对应的类中有Pointer字段b,b对应的Pointer字段c<br/>
     * 本次查询需要三个层级的查询,一直把c的详细信息查询出来.<br/><br/>
     *
     * 需要注意的是,系统默认会把"a.b.c"对应的所有层级的信息查询出来,不能只想要a和b的Pointer信息和c的详细信息.<br/>
     * 同时,当你需要查询a,b,c三个层级的信息时,只需要设置一个"a.b.c"到include中即可,不必同时设置{"a","a.b","a.b.c"}
     *
     * @param include
     */
    public void setInclude(List<String> include) {
        conditions.setInclude(include);
    }

    public Set<String> getSelectedKeys() {
        return conditions.getSelectedKeys();
    }

    /**
     * 设置需要查询的字段
     *
     * @param selectedKeys 字段集合
     */
    public void setSelectedKeys(Set<String> selectedKeys) {
        conditions.setSelectedKeys(selectedKeys);
    }

    /**
     * 这个只是针对传递过来json字符串，对于JBQueryConditions的where不适用
     *
     * @return string
     */
    public String getWhereSting() {
        return whereSting;
    }

    /**
     * 这个会把其他where条件全部替换
     *
     * @param whereSting 查询语句
     */
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

    /**
     * 设置查询返回文档数据的最大条数<br/>
     * limit取值为1-1000<br/>
     * 不设置默认为100
     * @param limit limit值
     * @return query
     */
    public JBQuery<T> setLimit(int limit) {
        conditions.setLimit(limit);
        return this;
    }

    /**
     * 设置查询返回文档数据的最大条数<br/>
     * limit取值为1-1000<br/>
     * 不设置默认为100
     * @param limit limit值
     * @return query
     */
    public JBQuery<T> limit(int limit) {
        setLimit(limit);
        return this;
    }

    public int getSkip() {
        return conditions.getSkip();
    }

    /**
     * 设置查询文档数据结果返回跳过的文档数目
     *
     * @param skip skip值
     * @return query
     */
    public JBQuery<T> setSkip(int skip) {
        conditions.setSkip(skip);
        return this;
    }

    /**
     * 设置查询文档数据结果返回跳过的文档数目
     *
     * @param skip skip值
     * @return query
     */
    public JBQuery<T> skip(int skip) {
        setSkip(skip);
        return this;
    }

    public LinkedHashMap<String, Integer> getOrder() {
        return conditions.getOrder();
    }

    /**
     * 设置查询排序方式
     *
     * @param order 排序方式
     * @return query
     */
    public JBQuery<T> setOrder(LinkedHashMap<String, Integer> order) {
        conditions.setOrder(order);
        return this;
    }

    /**
     * 设置查询排序方式
     *
     * @param order 排序方式
     * @return query
     */
    public JBQuery<T> order(LinkedHashMap<String, Integer> order) {
        setOrder(order);
        return this;
    }

    /**
     * 添加include的内容<br/><br/>
     *
     * 如果查询的字段是Pointer类型,通过设置include可以将Pointer字段的详细信息查询出来.<br/>
     * 例如有个字段为sound,音频,是Pointer类型,对应的class是Sound,则查询该字段的详细信息可以添加"sound"到include中.<br/>
     * 如果Sound还有有个字段为user,是Pointer类型,对应的class是_User,<br/>
     * 则查询sound字段对应的详细信息中的user字段的详细信息,可以添加"sound.user"到include中.<br/><br/>
     *
     * pointer字段的层级通过"."来分隔,例如"a.b.c",表示当前查询的主体类中有Pointer字段a,a对应的类中有Pointer字段b,b对应的Pointer字段c<br/>
     * 本次查询需要三个层级的查询,一直把c的详细信息查询出来.<br/><br/>
     *
     * 需要注意的是,系统默认会把"a.b.c"对应的所有层级的信息查询出来,不能只想要a和b的Pointer信息和c的详细信息.<br/>
     * 同时,当你需要查询a,b,c三个层级的信息时,只需要设置一个"a.b.c"到include中即可,不必分别设置"a","a.b","a.b.c"
     *
     * @param key key值
     * @return query
     */
    public JBQuery<T> include(String key) {
        conditions.include(key);
        return this;
    }

    /**
     * 添加正序排序字段<br/>
     * 不会删除之前设置的排序方式
     *
     * @param key 排序字段
     * @return query
     */
    public JBQuery<T> addAscendingOrder(String key) {
        conditions.addAscendingOrder(key);
        return this;
    }

    /**
     * 添加倒叙排序字段<br/>
     * 不会删除之前设置的排序方式
     *
     * @param key 排序字段
     * @return query
     */
    public JBQuery<T> addDescendingOrder(String key) {
        conditions.addDescendingOrder(key);
        return this;
    }

    /**
     * 添加待查询字段
     *
     * @param keys 待查询字段
     * @return     query
     */
    public JBQuery<T> selectKeys(Collection<String> keys) {
        conditions.selectKeys(keys);
        return this;
    }

    /**
     * 设置正序排序字段
     * 会把之前设置的排序方式删除
     *
     * @param key  排序字段
     * @return     query
     */
    public JBQuery<T> orderByAscending(String key) {
        conditions.orderByAscending(key);
        return this;
    }

    /**
     * 设置倒叙排序字段<br/>
     * 会把之前设置的排序方式删除
     *
     * @param key  排序字段
     * @return     query
     */
    public JBQuery<T> orderByDescending(String key) {
        conditions.orderByDescending(key);
        return this;
    }

    /**
     * 查询字段不为空<br/>
     * "$exists":true
     *
     * @param key 待查询字段
     * @return    query
     */
    public JBQuery<T> whereExists(String key) {
        conditions.whereExists(key);
        return this;
    }

    /**
     * 查询字段为空<br/>
     * "$exists":false
     *
     * @param key 待查询字段
     * @return    query
     */
    public JBQuery<T> whereNotExist(String key) {
        conditions.whereNotExist(key);
        return this;
    }

    /**
     * 查询字段值等于
     *
     * @param key   待查询字段
     * @param value 待比较值
     * @return      query
     */
    public JBQuery<T> whereEqualTo(String key, Object value) {
        conditions.whereEqualTo(key, value);
        return this;
    }

    /**
     * 查询字段值不等于<br/>
     * "$ne"
     *
     * @param key   待查询字段
     * @param value 待比较值
     * @return      query
     */
    public JBQuery<T> whereNotEqualTo(String key, Object value) {
        conditions.whereNotEqualTo(key, value);
        return this;
    }

    /**
     * 查询字段值大于等于<br/>
     * "$gte"
     *
     * @param key   待查询字段
     * @param value 待比较值
     * @return      query
     */
    public JBQuery<T> whereGreaterThanOrEqualTo(String key, Object value) {
        conditions.whereGreaterThanOrEqualTo(key, value);
        return this;
    }

    /**
     * 查询字段值大于<br/>
     * "$gt"
     *
     * @param key   待查询字段
     * @param value 待比较值
     * @return      query
     */
    public JBQuery<T> whereGreaterThan(String key, Object value) {
        conditions.whereGreaterThan(key, value);
        return this;
    }

    /**
     * 查询字段值小于<br/>
     * "$lt"
     *
     * @param key   待查询字段
     * @param value 待比较值
     * @return      query
     */
    public JBQuery<T> whereLessThan(String key, Object value) {
        conditions.whereLessThan(key, value);
        return this;
    }

    /**
     * 查询字段值小于等于<br/>
     * "$lte"
     *
     * @param key   待查询字段
     * @param value 待比较值
     * @return      query
     */
    public JBQuery<T> whereLessThanOrEqualTo(String key, Object value) {
        conditions.whereLessThanOrEqualTo(key, value);
        return this;
    }

    /**
     * 查询字段值包含某个字符串<br/>
     * ".*%s.*"
     *
     * @param key       待查询字段
     * @param substring 字符串
     * @return          query
     */
    public JBQuery<T> whereContains(String key, String substring) {
        conditions.whereContains(key, substring);
        return this;
    }

    /**
     * 查询字段值在集合中<br/>
     * "$in"
     *
     * @param key       待查询字段
     * @param values    集合
     * @return          query
     */
    public JBQuery<T> whereContainedIn(String key, Collection<? extends Object> values) {
        conditions.whereContainedIn(key, values);
        return this;
    }

    /**
     * 查询字段值不在集合中<br/>
     * "$nin"
     *
     * @param key       待查询字段
     * @param values    集合
     * @return          query
     */
    public JBQuery<T> whereNotContainedIn(String key, Collection<? extends Object> values) {
        conditions.whereNotContainedIn(key, values);
        return this;
    }

    /**
     * 查询字段以字符串开始<br/>
     * "^%s.*"
     *
     * @param key     待查询字段
     * @param prefix  待比较字符串
     * @return        query
     */
    public JBQuery<T> whereStartWith(String key, String prefix) {
        conditions.whereStartWith(key, prefix);
        return this;
    }

    /**
     * 查询字段以字符串结束<br/>
     * ".*%s$"
     *
     * @param key     待查询字段
     * @param suffix  待比较字符串
     * @return        query
     */
    public JBQuery<T> whereEndWith(String key, String suffix) {
        conditions.whereEndWith(key, suffix);
        return this;
    }

    /**
     * 查询字段匹配正则<br/>
     * "$regex"
     *
     * @param key     待查询字段
     * @param regex   正则表达式
     * @return        query
     */
    public JBQuery<T> whereMatches(String key, String regex) {
        conditions.whereMatches(key, regex);
        return this;
    }

    /**
     * 查询字段匹配正则<br/>
     * "$regex"<br/>
     * "$options"
     *
     * @param key       待查询字段
     * @param regex     正则表达式
     * @param modifiers 参数
     * @return          query
     */
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

    /**
     * 查询字段匹配子查询
     *
     * @param key       待查询字段
     * @param query     子查询
     * @return          query
     */
    public JBQuery<T> whereMatchesQuery(String key, JBQuery<?> query) {
        return whereMatchesKeyInQuery(key, null, query, null);
    }

    /**
     * 查询字段匹配子查询查询的字段
     *
     * @param key           待查询字段
     * @param searchKey     字查询查询字段
     * @param query         子查询
     * @param targetClass   字段对应的类名
     * @return
     */
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

    /**
     * 根据objectId查询对象 同步
     *
     * @param objectId      对象id
     * @return              对象
     * @throws JBException  信息
     */
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

    /**
     * 根据objectId查询对象 异步
     *
     * @param objectId  对象id
     * @param callback 查询结果回调
     */
    public void getInBackground(String objectId, JBGetCallback<T> callback) {
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
                    callback.done(false, null, new JBException(JBCode.OBJECT_NOT_EXIST));
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
     * 查询 同步
     *
     * @return              查询结果list
     * @throws JBException  异常信息
     */
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

    /**
     * 查询 异步
     * @param callBack 查询结果回调
     */
    public void findInBackground(JBFindCallBack<T> callBack) {
        findFromJavaBaas(false, callBack);
    }

    private void findFromJavaBaas(final boolean sync, final JBFindCallBack callback) {
        assembleParameters();
        String path = JBHttpClient.getObjectPath(this.className + "/find");
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, null, getParameters(), sync, new JBObjectCallback() {
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

    /**
     * 统计查询 同步
     *
     * @return              查询结果
     * @throws JBException  异常信息
     */
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

    /**
     * 统计查询 异步
     * @param callback 查询结果回调
     */
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

    /**
     * 批量删除 同步
     *
     * @throws JBException 异常信息
     */
    public void deleteByQuery() throws JBException {
        deleteByQueryFromJavabaas(true, new JBBooleanCallback() {
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
     * 批量删除 异步
     */
    public void deleteByQueryInBackground() {
        deleteByQueryFromJavabaas(false, null);
    }

    public void deleteByQueryInBackground(JBBooleanCallback callback) {
        deleteByQueryFromJavabaas(false, callback);
    }

    private void deleteByQueryFromJavabaas(final boolean sync, final JBBooleanCallback callback) {
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
            Map<String, Object> om = (Map<String, Object>) o;
            if (om.size() == 0) {
                return Collections.emptyList();
            }
            list = new ArrayList<>();
            list.add(om);
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
