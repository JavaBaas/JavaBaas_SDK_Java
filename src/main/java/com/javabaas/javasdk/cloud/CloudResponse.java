package com.javabaas.javasdk.cloud;

import java.util.Map;

/**
 * Created by Codi on 2018/7/23.
 */
public class CloudResponse extends JBResponse {

    private Map<String, ?> data;

    public Map<String, ?> getData() {
        return data;
    }

    public void setData(Map<String, ?> data) {
        this.data = data;
    }
}
