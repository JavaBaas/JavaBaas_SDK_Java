package com.javabaas.javasdk;

/**
 * 原子操作方式
 * Created by zangyilin on 2017/9/13.
 */
public enum JBOperatorType {

    DELETE("Delete"),
    ADD("Add"),
    ADDUNIQUE("AddUnique"),
    REMOVE("Remove"),
    INCREMENT("Increment"),
    MULTIPLY("Multiply");

    private String value;

    JBOperatorType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return value;
    }
}
