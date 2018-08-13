package com.javabaas.javasdk.cloud;

import com.javabaas.javasdk.JBCode;

import java.util.Map;

/**
 * Created by Codi on 2018/7/23.
 */
public class CloudResponse extends JBResponse {

    private Map<String, Object> data;

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public static CloudResponse success() {
        CloudResponse response = new CloudResponse();
        response.setCode(JBCode.SUCCESS.getCode());
        return response;
    }

    public static CloudResponse error(int code) {
        CloudResponse response = new CloudResponse();
        response.setCode(code);
        return response;
    }
}
