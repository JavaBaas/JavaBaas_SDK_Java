package com.javabaas.javasdk;

import com.javabaas.javasdk.callback.JBBooleanCallback;
import com.javabaas.javasdk.callback.JBObjectCallback;

import java.util.Map;

/**
 * 短信相关
 * Created by zangyilin on 2017/11/10.
 */
public class JBSms {
    /**
     * 发送短信 同步
     *
     * @param phone         手机号
     * @param templateId    短信模板
     * @param params        模板参数信息
     * @throws JBException  异常信息
     */
    public static void send(String phone, String templateId, Map<String, Object> params) throws JBException {
        sendToJavabaas(phone, templateId, params, true, new JBBooleanCallback() {
            @Override
            public void done(boolean success, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
    }

    /**
     * 发送短信 异步
     *
     * @param phone         手机号
     * @param templateId    短信模板
     * @param params        模板参数信息
     * @param callback      回调信息
     */
    public static void sendInBackground(String phone, String templateId, Map<String, Object> params, JBBooleanCallback callback) {
        sendToJavabaas(phone, templateId, params, false, callback);
    }

    private static void sendToJavabaas(final String phone, final String templateId, final Map<String, Object> body, final boolean sync, final JBBooleanCallback callback) {
        String path = JBHttpClient.getMasterPath("sms");
        JBHttpParams params = new JBHttpParams();
        params.put("phone", phone);
        params.put("templateId", templateId);
        sendSms(path, params, body, sync, callback);
    }

    /**
     * 发送短信验证码 同步<br/>
     * 短信验证码的模板id需要提前在JBAppConfig中配置好
     *
     * @param phone        手机号
     * @param expiresTime  过期时间
     * @param params       模板参数信息
     * @throws JBException 异常信息
     */
    public static void sendSmsCode(String phone, long expiresTime, Map<String, Object> params) throws JBException {
        sendSmsCodeToJavabaas(phone, expiresTime, params, true, new JBBooleanCallback() {
            @Override
            public void done(boolean success, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
    }

    /**
     * 发送短信验证码 异步<br/>
     * 短信验证码的模板id需要提前在JBAppConfig中配置好
     *
     * @param phone        手机号
     * @param expiresTime  过期时间
     * @param params       模板参数信息
     * @param callback     短信发送结果信息回调
     */
    public static void sendSmsCodeInBackground(String phone, long expiresTime, Map<String, Object> params, JBBooleanCallback callback){
        sendSmsCodeToJavabaas(phone, expiresTime, params, false, callback);
    }

    private static void sendSmsCodeToJavabaas(final String phone, final long expiresTime, final Map<String, Object> body,  boolean sync, final JBBooleanCallback callback){
        String path = JBHttpClient.getMasterPath("sms/smsCode");
        JBHttpParams params = new JBHttpParams();
        params.put("phone", phone);
        params.put("ttl", expiresTime);
        sendSms(path, params, body, sync, callback);
    }

    private static void sendSms (final String path, final JBHttpParams params, final Object body, final boolean sync, final JBBooleanCallback callback){
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, params, body, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                    return;
                }
                callback.done(true, null);
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, error);
                }
            }
        });
    }

    /**
     * 验证短信验证码 同步
     *
     * @param phone        手机号
     * @param code         短信验证码
     * @return             验证成功或失败
     * @throws JBException 异常信息
     */
    public static boolean validateSmsCode(String phone, String code) throws JBException {
        final boolean[] result = {false};
        validateSmsCodeFromJavabaas(phone, code, true, new JBBooleanCallback() {
            @Override
            public void done(boolean success, JBException e) {
                result[0] = success;
                if (e != null) {
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
     * 验证短信验证码 异步
     *
     * @param phone        手机号
     * @param code         短信验证码
     * @param callback     验证成功或失败回调
     */
    public static void validateSmsCodeInBackground(String phone, String code, JBBooleanCallback callback) {
        validateSmsCodeFromJavabaas(phone, code, false, callback);
    }

    private static void validateSmsCodeFromJavabaas(final String phone, final String code, final boolean sync, final JBBooleanCallback callback) {
        String path = JBHttpClient.getMasterPath("sms/verifyCode");
        JBHttpParams params = new JBHttpParams();
        params.put("phone", phone);
        params.put("code", code);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, params, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                    return;
                }
                boolean success = false;
                Map<String, Object> data = result.getData();
                if (data != null && data.get("verifyResult") != null) {
                    success = (boolean) data.get("verifyResult");
                }
                callback.done(success, null);
            }

            @Override
            public void onFailure(JBException error) {
                callback.done(false, error);
            }
        });
    }

}
