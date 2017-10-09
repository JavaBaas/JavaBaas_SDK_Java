package com.javabaas.javasdk;


import com.javabaas.javasdk.callback.JBObjectCallback;

/**
 * Created by zangyilin on 2017/8/15.
 */
public class JBPostHttpResponseHandler extends JBHttpResponseHandler {

    JBPostHttpResponseHandler(JBObjectCallback callback) {
        super(callback);
    }
    @Override
    public void onSuccess(JBResult result) {
        if (result.getCode() != 0) {
            if (getCallback() != null) {
                getCallback().onFailure(new JBException(result.getCode(), result.getMessage()));
            }
            return;
        } else {
            if (getCallback() != null) {
                getCallback().onSuccess(result);
            }
        }
    }

    @Override
    public void onFailure(JBException error) {
        if (getCallback() != null) {
            getCallback().onFailure(error);
        }
    }
}
