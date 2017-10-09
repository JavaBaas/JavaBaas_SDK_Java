package com.javabaas.javasdk;


import com.javabaas.javasdk.callback.JBObjectCallback;
import okhttp3.*;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Created by zangyilin on 2017/8/11.
 */
public class JBHttpClient {
    OkHttpClient client;
    public static final MediaType JSON = MediaType.parse("application/json");
    private static volatile JBHttpClient httpClient;
    public void sendRequest(String url, JBHttpMethod method, JBHttpParams params, Map<String, Object> body, final boolean sync, final JBObjectCallback callback) {
        String wholeUrl;
        if (params != null) {
            wholeUrl = params.getWholeUrl(url);
        } else {
            wholeUrl = url;
        }
        Request.Builder builder = getRequestBuilder();
        builder.url(wholeUrl);
        RequestBody requestBody = null;
        if (body != null) {
            requestBody = RequestBody.create(JSON, JBUtils.writeValueAsString(body));
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
        JBHttpResponseHandler handler = createPostHandler(callback);
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
                handler.onResponse(call, response);
            } catch (IOException e) {
                handler.onFailure(call, e);
            }
        } else {
            // 异步
             call.enqueue(handler);
        }
    }


    private Request.Builder getRequestBuilder() {
        if (!JBConfig.getInstance().finishInit) {
            throw new IllegalArgumentException("JBConfig未初始化");
        }
        long timestamp = new Date().getTime();
        String timestampStr = String.valueOf(timestamp);
        String nonce = UUID.randomUUID().toString().replace("-", "");

        Request.Builder builder = new Request.Builder();
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("JB-Plat", "cloud");
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

    private static String getPath(String domain, String extra) {
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



}
