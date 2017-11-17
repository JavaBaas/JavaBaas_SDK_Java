package com.javabaas.javasdk;

import com.javabaas.javasdk.callback.JBInstallationCallback;
import com.javabaas.javasdk.callback.JBObjectCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zangyilin on 2017/11/15.
 */
public class JBInstallation {



    /**
     * 设备注册，推送使用<br/>
     * 设备需要到javabaas上注册 Installation，并将返回的 InstallationId 注册到推送平台的别名中
     *
     * @param deviceType 设备类别 android或ios
     * @param deviceToken 设备deviceToken
     * @return InstallationId
     * @throws JBException 异常
     */
    public static String registerDevice(String deviceType, String deviceToken) throws JBException {
        final String[] result = {null};
        registerDeviceToJavabaas(deviceType, deviceToken, true, new JBInstallationCallback() {
            @Override
            public void done(boolean success, String installationId, JBException e) {
                if (success) {
                    result[0] = installationId;
                } else {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return result[0];
    }

    /**
     * 设备注册，推送使用<br/>
     * 设备需要到javabaas上注册 Installation，并将返回的 InstallationId 注册到推送平台的别名中
     *
     * @param deviceType 设备类别 android或ios
     * @param deviceToken 设备deviceToken
     * @param callback 设备注册回调
     */
    public static void registerDeviceInBackground(String deviceType, String deviceToken, JBInstallationCallback callback) {
        registerDeviceToJavabaas(deviceType, deviceToken, false, callback);
    }

    private static void registerDeviceToJavabaas(final String deviceType, final String deviceToken, final boolean sync, final JBInstallationCallback callback) {
        String path = JBHttpClient.getInstallationPath();
        Map<String, Object> body = new HashMap<>();
        body.put("deviceType", deviceType);
        body.put("deviceToken", deviceToken);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, null, body, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                    return;
                }
                if (result.getData() != null) {
                    String id = (String) result.getData().get("_id");
                    callback.done(true, id, null);
                } else {
                    callback.done(false, null, new JBException(JBCode.INTERNAL_ERROR.getCode(), "设备注册失败"));
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
