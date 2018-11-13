package com.javabaas.javasdk;

import java.util.*;

/**
 * Created by zangyilin on 2017/9/4.
 */
public class JBQueryConditions {

    public static final String WHERE = "where";
    public static final String LIMIT = "limit";
    public static final String SKIP = "skip";
    public static final String ORDER = "order";
    public static final String INCLUDE = "include";
    public static final String KEYS = "keys";

    Map<String, List<JBQueryOperation>> where;
    private List<String> include;
    private Set<String> selectedKeys;
    private int limit;
    private int skip = -1;
    private LinkedHashMap<String, Integer> order;
    private Map<String, String> parameters;

    public JBQueryConditions() {
        where = new HashMap<>();
        include = new LinkedList<>();
        parameters = new HashMap<>();
        order = new LinkedHashMap<>();
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public LinkedHashMap<String, Integer> getOrder() {
        return order;
    }

    public void setOrder(LinkedHashMap<String, Integer> order) {
        this.order = order;
    }

    public Map<String, List<JBQueryOperation>> getWhere() {
        return where;
    }

    public void setWhere(Map<String, List<JBQueryOperation>> where) {
        this.where = where;
    }

    public List<String> getInclude() {
        return include;
    }

    public void setInclude(List<String> include) {
        this.include = include;
    }

    public Set<String> getSelectedKeys() {
        return selectedKeys;
    }

    public void setSelectedKeys(Set<String> selectedKeys) {
        this.selectedKeys = selectedKeys;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public void addAscendingOrder(String key) {
        order.remove(key);
        order.put(key, 1);
    }

    public void orderByAscending(String key) {
        order = new LinkedHashMap<>();
        order.put(key, 1);
    }

    public void addDescendingOrder(String key) {
        order.remove(key);
        order.put(key, -1);
    }

    public void orderByDescending(String key) {
        order = new LinkedHashMap<>();
        order.put(key, -1);
    }

    public void include(String key) {
        include.add(key);
    }

    public void selectKeys(Collection<String> keys) {
        if (selectedKeys == null) {
            selectedKeys = new HashSet<>();
        }
        selectedKeys.addAll(keys);
    }

    public Map<String, Object> compileWhereOperationMap() {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, List<JBQueryOperation>> entry : where.entrySet()) {
            if (entry.getKey().equals(JBQueryOperation.OR_OP)) {
                List<Object> opList = new ArrayList<>();
                for (JBQueryOperation op : entry.getValue()) {
                    opList.add(op.toResult());
                }
                List<Object> existsOr = (List<Object>) result.get(JBQueryOperation.OR_OP);
                if (existsOr != null) {
                    existsOr.addAll(opList);
                } else {
                    result.put(JBQueryOperation.OR_OP, opList);
                }
            } else if (entry.getKey().equals(JBQueryOperation.AND_OP)) {
                List<Object> opList = new ArrayList<>();
                for (JBQueryOperation op : entry.getValue()) {
                    opList.add(op.getValue());
                }
                List<Object> existsAnd = (List<Object>) result.get(JBQueryOperation.AND_OP);
                if (existsAnd != null) {
                    existsAnd.addAll(opList);
                } else {
                    result.put(JBQueryOperation.AND_OP, opList);
                }
            } else {
                switch (entry.getValue().size()) {
                    case 0:
                        break;
                    case 1:
                        for (JBQueryOperation op : entry.getValue()) {
                            result.put(entry.getKey(), op.toResult());
                        }
                        break;
                    default:
                        List<Object> opList = new ArrayList<>();
                        Map<String, Object> opMap = new HashMap<>();
                        final boolean[] hasEqual = {false};
                        for (JBQueryOperation op : entry.getValue()) {
                            opList.add(op.toResult(entry.getKey()));
                            if (JBQueryOperation.EQUAL_OP.equals(op.getOp())) {
                                hasEqual[0] = true;
                            }
                            if (!hasEqual[0]) {
                                opMap.putAll((Map<? extends String, ?>) op.toResult());
                            }
                        }
                        if (hasEqual[0]) {
                            List<Object> existsAnd = (List<Object>) result.get(JBQueryOperation.AND_OP);
                            if (existsAnd != null) {
                                existsAnd.addAll(opList);
                            } else {
                                result.put(JBQueryOperation.AND_OP, opList);
                            }
                        } else {
                            result.put(entry.getKey(), opMap);
                        }
                        break;
                }
            }
        }
        return result;
    }

    public Map<String, String> assembleParameters() {
        if (where.size() > 0) {
            try {
                parameters.put(WHERE, JBUtils.writeValueAsString(compileWhereOperationMap()));
            } catch (JBException e) {
            }
        }
        if (limit > 0) {
            parameters.put(LIMIT, String.valueOf(limit));
        }
        if (skip > 0) {
            parameters.put(SKIP, String.valueOf(skip));
        }
        if (order.size() > 0) {
            try {
                parameters.put(ORDER, JBUtils.writeValueAsString(order));
            } catch (JBException e) {
            }
        }
        if (include != null && include.size() > 0) {
            parameters.put(INCLUDE, join(include, ","));
        }
        if (selectedKeys != null && selectedKeys.size() > 0) {
            parameters.put(KEYS, join(new ArrayList<>(selectedKeys), ","));
        }
        return parameters;
    }

    private String join(List<String> list, String conjunction) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : list)
        {
            if (first)
                first = false;
            else
                sb.append(conjunction);
            sb.append(item);
        }
        return sb.toString();
    }

    public void addWhereItem(JBQueryOperation op) {
        List<JBQueryOperation> ops = where.get(op.getKey());
        if (ops == null) {
            ops = new LinkedList<>();
            where.put(op.getKey(), ops);
        }
        removeSameOperation(ops, op);
        ops.add(op);
    }

    public void addWhereItem(String key, String op, Object value) {
        addWhereItem(new JBQueryOperation(key, value, op));
    }

    public void addOrItems(JBQueryOperation op) {
        List<JBQueryOperation> ops = where.get(JBQueryOperation.OR_OP);
        if (ops == null) {
            ops = new LinkedList<>();
            where.put(JBQueryOperation.OR_OP, ops);
        }
        removeSameOperation(ops, op);
        ops.add(op);
    }

    public void addAndItems(JBQueryConditions conditions) {
        Map<String, Object> queryOperationMap = conditions.compileWhereOperationMap();
        JBQueryOperation op = new JBQueryOperation(JBQueryOperation.AND_OP, queryOperationMap, JBQueryOperation.AND_OP);
        List<JBQueryOperation> ops = where.get(JBQueryOperation.AND_OP);
        if (ops == null) {
            ops = new LinkedList<>();
            where.put(JBQueryOperation.AND_OP, ops);
        }
        removeSameOperation(ops, op);
        ops.add(op);
    }

    private void removeSameOperation(List<JBQueryOperation> ops, JBQueryOperation op) {
        Iterator<JBQueryOperation> iterator = ops.iterator();
        while (iterator.hasNext()) {
            JBQueryOperation operation = iterator.next();
            if (operation.sameOp(op)) {
                iterator.remove();
            }
        }
    }

    public void whereEqualTo(String key, Object value) {
        if (value instanceof JBObject) {
            JBObject object = (JBObject) value;
            if (!JBUtils.isEmpty(object.className) && !JBUtils.isEmpty(object.objectId)) {
                Map<String, Object> pointer = new LinkedHashMap<>();
                pointer.put("__type", "Pointer");
                pointer.put("className", object.className);
                pointer.put("_id", object.objectId);
                addWhereItem(key, JBQueryOperation.EQUAL_OP, pointer);
            }
        } else {
            addWhereItem(key, JBQueryOperation.EQUAL_OP, value);
        }
    }

    public void whereNotEqualTo(String key, Object value) {
        addWhereItem(key, "$ne", value);
    }

    public void whereExists(String key) {
        addWhereItem(key, "$exists", true);
    }

    public void whereNotExist(String key) {
        addWhereItem(key, "$exists", false);
    }

    public void whereGreaterThanOrEqualTo(String key, Object value) {
        addWhereItem(key, "$gte", value);
    }

    public void whereGreaterThan(String key, Object value) {
        addWhereItem(key, "$gt", value);
    }

    public void whereLessThan(String key, Object value) {
        addWhereItem(key, "$lt", value);
    }

    public void whereLessThanOrEqualTo(String key, Object value) {
        addWhereItem(key, "$lte", value);
    }

    public void whereContainedIn(String key, Collection<? extends Object> values) {
        addWhereItem(key, "$in", values);
    }

    public void whereNotContainedIn(String key, Collection<? extends Object> values) {
        addWhereItem(key, "$nin", values);
    }

    public void whereStartWith(String key, String prefix) {
        whereMatches(key, String.format("^%s.*", prefix));
    }

    public void whereEndWith(String key, String suffix) {
        whereMatches(key, String.format(".*%s$", suffix));
    }

    public void whereContains(String key, String substring) {
        whereMatches(key, String.format(".*%s.*", substring));
    }

    public void whereMatches(String key, String regex) {
        addWhereItem(key, "$regex", regex);
    }

    public void whereMatches(String key, String regex, String modifiers) {
        addWhereItem(key, "$regex", regex);
        addWhereItem(key, "$options", modifiers);
    }


}
