package com.javabaas.javasdk.cloud;

import java.util.Map;

/**
 * Created by Codi on 2018/7/18.
 */
public class CloudRequest extends JBRequest {

    private String name;
    private Map<String, String> params;
    private String body;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String requestType() {
        return JBRequest.REQUEST_CLOUD;
    }
}
