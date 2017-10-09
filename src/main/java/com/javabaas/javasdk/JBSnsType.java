package com.javabaas.javasdk;

/**
 * Created by zangyilin on 2017/9/19.
 */
public enum JBSnsType {
    WEIBO(1, "weibo"),
    QQ(2, "qq"),
    WEIXIN(3, "wx"),
    WEBAPP(4, "wx");

    private int code;
    private String value;

    JBSnsType(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return this.code;
    }

    public String getValue() {
        return this.value;
    }

    public static JBSnsType getType(int code) {
        for (JBSnsType type : JBSnsType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return super.toString() + " code:" + this.code + " value:" + this.value;
    }
}
