package com.javabaas.javasdk;


import com.javabaas.javasdk.callback.JBObjectCallback;
import okhttp3.*;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by zangyilin on 2017/8/11.
 */
public class JBHttpClient {
    OkHttpClient client;
    public static final MediaType JSON = MediaType.parse("application/json");
    private static volatile JBHttpClient httpClient;

    public void sendRequest(String url, JBHttpMethod method, JBHttpParams params, Object body, final boolean sync, final JBObjectCallback callback) {
        JBHttpResponseHandler handler = createPostHandler(callback);
        Request.Builder builder;
        try {
            builder = getRequestBuilder();
        } catch (JBException e) {
            handler.onFailure(e);
            return;
        }

        String wholeUrl;
        if (params != null) {
            wholeUrl = params.getWholeUrl(url);
        } else {
            wholeUrl = url;
        }
        RequestBody requestBody;
        builder.url(wholeUrl);
        if (body != null) {
            try {
                requestBody = RequestBody.create(JSON, JBUtils.writeValueAsString(body));
            } catch (JBException e) {
                handler.onFailure(e);
                return;
            }
        } else {
            requestBody = RequestBody.create(JSON, "{}");
        }
        switch (method) {
            case GET:
                break;
            case PUT:
                if (requestBody != null) {
                    builder.put(requestBody);
                }
                break;
            case POST:
                if (requestBody != null) {
                    builder.post(requestBody);
                }
                break;
            case DELETE:
                builder.delete();
                break;
            default:
        }
        httpClient.execute(builder.build(), sync, handler);
    }

    private JBHttpResponseHandler createPostHandler(JBObjectCallback callback) {
        JBHttpResponseHandler handler = new JBPostHttpResponseHandler(callback);
        return handler;
    }

    public static synchronized JBHttpClient INSTANCE() {
        if (httpClient == null) {
            httpClient = new JBHttpClient();
        }
        return httpClient;
    }

    public JBHttpClient() {
        client = new OkHttpClient();
    }

    public void execute(Request request, boolean sync, final JBHttpResponseHandler handler) {
        Call call = client.newCall(request);
        if (sync) {
            // 同步
            try {
                Response response = call.execute();
                handler.onResponse(call, response, true);
            } catch (IOException e) {
                handler.onFailure(call, e, true);
            }
        } else {
            // 异步
             call.enqueue(handler);
        }
    }


    private Request.Builder getRequestBuilder() throws JBException {
        if (!JBConfig.getInstance().finishInit) {
            throw new JBException(JBCode.INTERNAL_ERROR.getCode(), "JBConfig未初始化");
        }
        long timestamp = new Date().getTime();
        String timestampStr = String.valueOf(timestamp - JBConfig.getInstance().adjustTime);
        String nonce = UUID.randomUUID().toString().replace("-", "");

        Request.Builder builder = new Request.Builder();
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("JB-Plat", JBConfig.getInstance().plat);
        builder.addHeader("JB-Timestamp", timestampStr);
        builder.addHeader("JB-Nonce", nonce);
        String appId = JBConfig.getInstance().appId;
        if (!JBUtils.isEmpty(appId)) {
            builder.addHeader("JB-AppId", JBConfig.getInstance().appId);
        }
        String key = JBConfig.getInstance().key;
        if (!JBUtils.isEmpty(key)) {
            builder.addHeader("JB-Sign", getSign(key, timestampStr, nonce));
        }
        String masterKey = JBConfig.getInstance().masterKey;
        if (!JBUtils.isEmpty(masterKey)) {
            builder.addHeader("JB-MasterSign", getSign(masterKey, timestampStr, nonce));
        }
        String adminKey = JBConfig.getInstance().adminKey;
        if (!JBUtils.isEmpty(adminKey)) {
            builder.addHeader("JB-AdminSign", getSign(adminKey, timestampStr, nonce));
        }
        if (JBUser.getCurrentUser() != null && !JBUtils.isEmpty(JBUser.getCurrentUser().getObjectId())) {
            builder.addHeader("JB-SessionToken", JBUser.getCurrentUser().getObjectId());
        }
        return builder;
    }

    public String getSign(String key, String timestamp, String nonce) {
        return JBUtils.md5DigestAsHex(key + ":" + timestamp + ":" + nonce);
    }

    public static String getUserPath() {
        return getPath("user", null);
    }

    public static String getUserPath(String extra) {
        return getPath("user", extra);
    }

    public static String getObjectPath(String className) {
        return getPath("object", className);
    }

    public static String getObjectPath(String className, String extra) {
        return getPath("object", className + "/" + extra);
    }

    public static String getInstallationPath() {
        return getPath("installation", null);
    }

    public static String getAdminPath() {
        return getPath("admin/app", null);
    }

    public static String getAdminPath(String extra) {
        return getPath("admin/app", extra);
    }

    public static String getClazzPath() {
        return getPath("master/clazz", null);
    }

    public static String getClazzPath(String extra) {
        return getPath("master/clazz", extra);
    }

    public static String getFieldPath(String className) {
        return getPath("master/clazz/" + className + "/field", null);
    }

    public static String getFieldPath(String className, String extra) {
        return getPath("master/clazz/" + className + "/field", extra);
    }

    public static String getMasterPath(String extra) {
        return getPath("master", extra);
    }

    public static String getFilePath(String extra) {
        return getPath("file", extra);
    }

    public static String getCloudPath(String extra) {
        return getPath("cloud", extra);
    }

    public static String getCloudDeployPath() {
        return getPath("master/cloud", null);
    }

    public static String getConfigPath() {
        return getPath("master/config", null);
    }

    public static String getConfigPath(String extra) {
        return getPath("master/config", extra);
    }

    public static String getApiStatPath() {
        return getPath("master/apiStat", null);
    }

    public static String getAccountPath(int type) {
        return getPath("master/account/setAccount", String.valueOf(type));
    }

    private static String getPath(String domain, String extra) {
        if (JBUtils.isEmpty(JBConfig.getInstance().remote)) {
            return null;
        }
        StringBuffer urlPath =  new StringBuffer(JBConfig.getInstance().remote);
        if (JBUtils.isEmpty(domain)) {
            return null;
        } else {
            urlPath.append(urlPath.toString().endsWith("/")? "" : "/");
            urlPath.append(domain).append("/");
        }
        if (!JBUtils.isEmpty(extra)) {
            urlPath.append(extra);
        }
        return urlPath.toString();
    }


    public static String getStatusPath() {
        return JBConfig.getInstance().remote;
    }


}
