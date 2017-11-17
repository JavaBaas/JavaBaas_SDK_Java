package com.javabaas.javasdk;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * operator操作相关
 * Created by zangyilin on 2017/9/13.
 */
public class JBOperator extends LinkedHashMap<String, Object> {
    private JBOperator(){}

    public JBOperator(JBOperatorType type) {
        super();
        this.put("__op", type.getValue());
    }

    public JBOperatorType getOp() {
        Object op = get("__op");
        return op == null ? null : JBOperatorType.valueOf(op.toString().toUpperCase());
    }

    public void setAmount(long amount) {
        this.put("amount", amount);
    }

    public long getAmount() {
        Object value = this.get("amount");
        return value == null ? 0 : Long.valueOf(value.toString());
    }

    public void setObjects(List<Object> objects) {
        this.put("objects", objects);
    }

    public List<Object> getObjects() {
        Object value = this.get("objects");
        return value == null? new LinkedList<>() : (List<Object>) value;
    }

}
