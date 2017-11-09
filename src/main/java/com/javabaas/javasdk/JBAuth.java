package com.javabaas.javasdk;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zangyilin on 2017/9/19.
 */
public class JBAuth extends LinkedHashMap<String, Object> {
    public JBAuth(Map<String, Object> m) {
        super(m);
    }

    public JBAuth() {
        super();
    }

    public void setAccessToken(String accessToken) {
        put("accessToken", accessToken);
    }

    public String getAccessToken() {
        return (String) get("accessToken");
    }

    public String getUid() {
        return (String) get("uid");
    }

    public void setUid(String uid) {
        put("uid", uid);
    }

    public String getOpenId() {
        return (String) get("openId");
    }

    public void setOpenId(String openId) {
        put("openId", openId);
    }

    public String getUnionId() {
        return (String) get("unionId");
    }

    public void setUnionId(String unionId) {
        put("unionId", unionId);
    }

    public void setEncryptedData(String encryptedData) {
        put("encryptedData", encryptedData);
    }

    public String getEncryptedData() {
        return (String) get("encryptedData");
    }

    public void setCode(String code) {
        put("code", code);
    }

    public String getCode() {
        return (String) get("code");
    }

    public void setIv(String iv) {
        put("iv", iv);
    }

    public String getIV() {
        return (String) get("iv");
    }
}
