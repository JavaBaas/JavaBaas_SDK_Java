package com.javabaas.javasdk.callback;


import com.javabaas.javasdk.JBException;
import com.javabaas.javasdk.JBObject;
import com.javabaas.javasdk.JBResult;

/**
 * Created by zangyilin on 2017/8/15.
 */
public abstract class JBObjectCallback {
    public void onSuccess(JBResult result) {}

    public void onFailure(int statusCode, JBException error, String content) {
        onFailure(error);
    }

    public void onFailure(JBException error) {}

    public void onGroupRequestFinished(int left, int total, JBObject object) {}

    public void retry(JBException error, String content){}


}
