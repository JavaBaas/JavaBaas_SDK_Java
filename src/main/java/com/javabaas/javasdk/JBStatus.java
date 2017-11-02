package com.javabaas.javasdk;

import com.javabaas.javasdk.callback.JBObjectCallback;
import com.javabaas.javasdk.callback.JBStatusCallback;

import java.util.Map;

/**
 * Created by zangyilin on 2017/10/30.
 */
public class JBStatus {
    public static Map<String, Object> getStatus() throws JBException{
        final Map<String, Object>[] map = new Map[]{null};
        getStatusFromJavabaas(true, new JBStatusCallback() {
            @Override
            public void done(boolean success, Map<String, Object> status, JBException e) {
                if (success) {
                    map[0] = status;
                } else {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return map[0];
    }

    public static void getStatusInBackground(JBStatusCallback callback) {
        getStatusFromJavabaas(false, callback);
    }

    private static void getStatusFromJavabaas(final boolean sync, final JBStatusCallback callback) {
        String path = JBHttpClient.getStatusPath();
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, null, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                    return;
                }
                if (result.getData() == null || result.getData().get("result") == null || !(result.getData().get("result") instanceof Map)) {
                    callback.done(false, null, new JBException(JBCode.INTERNAL_JSON_ERROR.getCode(), "服务器返回信息错误"));
                } else {
                    callback.done(true, (Map<String, Object>) result.getData().get("result"), null);
                }
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, null, error);
                }
            }
        });
    }
}
