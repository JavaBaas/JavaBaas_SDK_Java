package com.javabaas.javasdk.cloud;

import com.javabaas.javasdk.JBCode;
import com.javabaas.javasdk.JBException;
import com.javabaas.javasdk.JBUtils;

import java.util.Map;
import java.util.Optional;

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

    public Optional<String> getOptionalParam(String key) {
        return getParams() == null ? null : Optional.ofNullable(getParams().get(key));
    }

    public String getRequiredParam(String key) throws JBException {
        if (getParams() == null) {
            throw new JBException(JBCode.REQUEST_PARAM_ERROR);
        } else {
            String value = getParams().get(key);
            if (JBUtils.isEmpty(value)) {
                throw new JBException(JBCode.REQUEST_PARAM_ERROR);
            } else  {
                return value;
            }
        }
    }

    public int getRequiredIntParam(String key) throws JBException {
        if (getParams() == null) {
            throw new JBException(JBCode.REQUEST_PARAM_ERROR);
        } else {
            String value = getParams().get(key);
            if (JBUtils.isEmpty(value)) {
                throw new JBException(JBCode.REQUEST_PARAM_ERROR);
            } else  {
                try {
                    int num = Integer.parseInt(value);
                    return num;
                } catch (NumberFormatException e) {
                    throw new JBException(JBCode.REQUEST_PARAM_ERROR);
                }
            }
        }
    }

    public boolean getRequiredBooleanParam(String key) throws JBException {
        if (getParams() == null) {
            throw new JBException(JBCode.REQUEST_PARAM_ERROR);
        } else {
            String value = getParams().get(key);
            if (JBUtils.isEmpty(value)) {
                throw new JBException(JBCode.REQUEST_PARAM_ERROR);
            } else  {
                try {
                    boolean bool = Boolean.parseBoolean(value);
                    return bool;
                } catch (NumberFormatException e) {
                    throw new JBException(JBCode.REQUEST_PARAM_ERROR);
                }
            }
        }
    }

    @Override
    public String requestType() {
        return JBRequest.REQUEST_CLOUD;
    }
}
