package com.javabaas.javasdk;

import com.javabaas.javasdk.callback.JBBooleanCallback;
import com.javabaas.javasdk.callback.JBObjectCallback;
import com.javabaas.javasdk.callback.JBSendCallback;

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
     * @return              短信发送结果信息
     * @throws JBException  异常信息
     */
    public static JBSmsSendResult send(String phone, String templateId, Map<String, Object> params) throws JBException {
        final JBSmsSendResult[] sendResult = {null};
        sendToJavabaas(phone, templateId, params, true, new JBSendCallback() {
            @Override
            public void done(boolean success, JBSmsSendResult result, JBException e) {
                if (success) {
                    sendResult[0] = result;
                } else {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return sendResult[0];
    }

    /**
     * 发送短信 异步
     *
     * @param phone         手机号
     * @param templateId    短信模板
     * @param params        模板参数信息
     * @param callback      回调信息
     */
    public static void sendInBackground(String phone, String templateId, Map<String, Object> params, JBSendCallback callback) {
        sendToJavabaas(phone, templateId, params, false, callback);
    }

    private static void sendToJavabaas(final String phone, final String templateId, final Map<String, Object> body, final boolean sync, final JBSendCallback callback) {
        String path = JBHttpClient.getMasterPath("sms");
        JBHttpParams params = new JBHttpParams();
        params.put("phone", phone);
        params.put("templateId", templateId);
        sendSms(path, params, body, sync, callback);
    }

    /**
     * 发送短信验证码 同步
     * 短信验证码的模板id需要提前在JBAppConfig中配置好
     *
     * @param phone        手机号
     * @param expiresTime  过期时间
     * @param params       模板参数信息
     * @return             短信发送结果信息
     * @throws JBException 异常信息
     */
    public static JBSmsSendResult sendSmsCode(String phone, long expiresTime, Map<String, Object> params) throws JBException {
        final JBSmsSendResult[] sendResult = {null};
        sendSmsCodeToJavabaas(phone, expiresTime, params, true, new JBSendCallback() {
            @Override
            public void done(boolean success, JBSmsSendResult result, JBException e) {
                if (success) {
                    sendResult[0] = result;
                } else {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return sendResult[0];
    }

    /**
     * 发送短信验证码 异步
     * 短信验证码的模板id需要提前在JBAppConfig中配置好
     *
     * @param phone        手机号
     * @param expiresTime  过期时间
     * @param params       模板参数信息
     * @param callback     短信发送结果信息回调
     */
    public static void sendSmsCodeInBackground(String phone, long expiresTime, Map<String, Object> params, JBSendCallback callback){
        sendSmsCodeToJavabaas(phone, expiresTime, params, false, callback);
    }

    private static void sendSmsCodeToJavabaas(final String phone, final long expiresTime, final Map<String, Object> body,  boolean sync, final JBSendCallback callback){
        String path = JBHttpClient.getMasterPath("sms/smsCode");
        JBHttpParams params = new JBHttpParams();
        params.put("phone", phone);
        params.put("ttl", expiresTime);
        sendSms(path, params, body, sync, callback);
    }

    private static void sendSms (final String path, final JBHttpParams params, final Object body, final boolean sync, final JBSendCallback callback){
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, params, body, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                    return;
                }
                Map<String, Object> data = result.getData();
                if (data != null && data.get("sendResult") != null) {
                    try {
                        String value = JBUtils.writeValueAsString(data.get("sendResult"));
                        JBSmsSendResult smsSendResult = JBUtils.readValue(value, JBSmsSendResult.class);
                        callback.done(true, smsSendResult, null);
                        return;
                    } catch (JBException e) {
                    }
                }
                callback.done(false, null, new JBException(JBCode.REQUEST_JSON_ERROR.getCode(), "发送失败"));
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, null, error);
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

    /**
     * 短信发送结果
     */
    public static class JBSmsSendResult {
        private int Code;
        private String message;

        public JBSmsSendResult(int code, String message) {
            Code = code;
            this.message = message;
        }

        public static JBSmsSendResult success() {
            return new JBSmsSendResult(SmsSendResultCode.SUCCESS.getCode(), SmsSendResultCode.SUCCESS.getMessage());
        }

        public static JBSmsSendResult error(SmsSendResultCode code) {
            return new JBSmsSendResult(code.getCode(), code.getMessage());
        }

        public int getCode() {
            return Code;
        }

        public void setCode(int code) {
            Code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    /**
     * 短信发送结果返回码
     */
    public enum SmsSendResultCode {

        SUCCESS(0, "成功"),
        ILLEGAL_PHONE_NUMBER(1, "手机号不合法"),
        AMOUNT_NOT_ENOUGH(2, "余额不足"),
        INVALID_PARAM(3, "参数不合法"),
        TEMPLATE_MISSING_PARAMETERS(4, "模版缺少变量"),
        OTHER_ERRORS(5, "其他错误 请查看日志");

        private int code;
        private String message;

        SmsSendResultCode(int code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String toString() {
            return message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }


}
