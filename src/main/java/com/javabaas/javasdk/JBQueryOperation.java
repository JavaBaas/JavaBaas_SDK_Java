package com.javabaas.javasdk;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zangyilin on 2017/9/4.
 */
public class JBQueryOperation {
    public static final String EQUAL_OP = "__eq";
    public static final String OR_OP = "$or";
    public static final String AND_OP = "$and";

    private String key;
    private Object value;
    private String op;

    public JBQueryOperation(String key, Object value, String op) {
        this.key = key;
        this.value = value;
        this.op = op;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public String getOp() {
        return op;
    }

    public Object toResult() {
        if (op == null || op.equals(EQUAL_OP) || op.equals(OR_OP)) {
            return value;
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put(op, value);
            return map;
        }
    }

    public Object toResult(String key) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, this.toResult());
        return map;
    }

    public boolean sameOp(JBQueryOperation other) {
        return JBUtils.equals(this.key, other.key) && JBUtils.equals(this.op, other.op);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((op == null) ? 0 : op.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JBQueryOperation other = (JBQueryOperation) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (op == null) {
            if (other.op != null)
                return false;
        } else if (!op.equals(other.op))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

}
