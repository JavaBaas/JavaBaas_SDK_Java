package com.javabaas.javasdk;

import com.javabaas.javasdk.callback.JBObjectCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by zangyilin on 2017/8/15.
 */
public abstract class JBHttpResponseHandler implements Callback {
    protected JBObjectCallback callback;

    public JBHttpResponseHandler(JBObjectCallback callback) {
        this.callback = callback;
    }

    public JBObjectCallback getCallback() {
        return callback;
    }

    public void setCallback(JBObjectCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        onFailure(call, e, false);
    }

    @Override
    public void onResponse(Call call, final Response response) throws IOException {
        onResponse(call, response, false);
    }

    public void onFailure(Call call, IOException e, boolean sync) {
        if (!sync) {
            //异步操作
            if (checkAndroid()) {
                //安卓环境
                androidInvoke(new Runnable() {
                    @Override
                    public void run() {
                        JBHttpResponseHandler.this.onFailure(new JBException(JBCode.OTHER_HTTP_ERROR));
                    }
                });
            } else {
                //非安卓环境
                this.onFailure(new JBException(JBCode.OTHER_HTTP_ERROR));
            }
        } else {
            //同步操作
            this.onFailure(new JBException(JBCode.OTHER_HTTP_ERROR));
        }
    }

    public void onResponse(Call call, final Response response, boolean sync) throws IOException {
        final String content = JBUtils.stringFromBytes(response.body().bytes());
        if (!sync) {
            //异步操作
            if (checkAndroid()) {
                //安卓环境
                androidInvoke(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (response.code() == 200) {
                                JBResult result = JBUtils.readValue(content, JBResult.class);
                                JBHttpResponseHandler.this.onSuccess(result);
                            } else if (response.code() == 400 || response.code() == 500) {
                                JBResult result = JBUtils.readValue(content, JBResult.class);
                                JBHttpResponseHandler.this.onFailure(new JBException(result.getCode(), result.getMessage()));
                            } else {
                                JBHttpResponseHandler.this.onFailure(new JBException(JBCode.OTHER_HTTP_ERROR));
                            }
                        } catch (JBException e) {
                            JBHttpResponseHandler.this.onFailure(e);
                        }
                    }
                });
            } else {
                //非安卓环境
                response(response, content);
            }
        } else {
            //同步操作
            response(response, content);
        }
    }

    private void androidInvoke(final Runnable runnable) {
        try {
            //在安卓环境中使用handler处理回调 确保返回到主线程
            Class<?> HandlerClass = Class.forName("android.os.Handler");
            Class<?> LopperClass = Class.forName("android.os.Looper");
            Object mainLopper = LopperClass.getMethod("getMainLooper").invoke(null);
            Constructor<?> handlerConstructor = HandlerClass.getConstructor(LopperClass);
            Object handler = handlerConstructor.newInstance(mainLopper);
            Method method = HandlerClass.getMethod("post", Runnable.class);
            method.invoke(handler, (runnable));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void response(Response response, String content) {
        try {
            if (response.code() == 200) {
                JBResult result = JBUtils.readValue(content, JBResult.class);
                this.onSuccess(result);
            } else if (response.code() == 400 || response.code() == 500) {
                JBResult result = JBUtils.readValue(content, JBResult.class);
                this.onFailure(new JBException(result.getCode(), result.getMessage()));
            } else {
                this.onFailure(new JBException(JBCode.OTHER_HTTP_ERROR));
            }
        } catch (JBException e) {
            this.onFailure(e);
        }
    }

    private boolean checkAndroid() {
        try {
            //判断是否存在安卓环境 在安卓环境中使用handler处理回调 确保返回到主线程
            Class.forName("android.os.Handler");
            return true;
        } catch (ClassNotFoundException ignore) {
            //类不存在 非安卓环境
            return false;
        }
    }

    public abstract void onSuccess(JBResult result);

    public abstract void onFailure(JBException error);

}
