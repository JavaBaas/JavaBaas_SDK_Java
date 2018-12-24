package com.javabaas.javasdk;

import com.javabaas.javasdk.callback.JBBooleanCallback;
import com.javabaas.javasdk.callback.JBObjectCallback;

import java.util.Map;

/**
 * 推送相关
 * Created by zangyilin on 2017/11/10.
 */
public class JBPush {
    // 透传信息，静默推送
    private JBPushMessage message;
    // 推送信息，可达通知栏
    private JBPushNotification notification;

    public JBPushMessage getMessage() {
        return message;
    }

    public void setMessage(JBPushMessage message) {
        this.message = message;
    }

    public JBPushNotification getNotification() {
        return notification;
    }

    public void setNotification(JBPushNotification notification) {
        this.notification = notification;
    }

    /**
     * 发送推送/透传信息 同步
     *
     * @param query         过滤条件,过滤installationId
     * @return              成功或失败
     * @throws JBException  异常信息
     */
    public boolean sendPush(JBPushQuery query) throws JBException {
        sendPushToJavabaas(query, true, new JBBooleanCallback() {
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
        return true;
    }

    /**
     * 发送推送/透传信息 异步
     *
     * @param query         过滤条件,过滤installationId
     * @param callback      成功或失败回调信息
     */
    public void sendPushInBackground(JBPushQuery query, JBBooleanCallback callback) {
        sendPushToJavabaas(query, false, callback);
    }

    private void sendPushToJavabaas(final JBPushQuery query, final boolean sync, final JBBooleanCallback callback) {
        if (this.message == null && this.notification == null) {
            if (callback != null) {
                callback.done(false, new JBException(JBCode.PUSH_ERROR));
            }
            return;
        }
        String path = JBHttpClient.getMasterPath("push");
        query.assembleParameters();
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, new JBHttpParams(query.getParameters()), this, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback != null) {
                    callback.done(true, null);
                }
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, error);
                }
            }
        });
    }

    public static class JBPushQuery extends JBQuery {
        public JBPushQuery() {
            super("_Installation");
        }
    }

    /**
     * Created by test on 2017/6/27.
     * <p>
     * 应用内消息。或者称作：自定义消息，透传消息。
     * 此部分内容不会展示到通知栏上。
     */
    public static class JBPushMessage {

        // 消息标题
        private String title;
        // 消息内容本身
        private String alert;
        // 消息内容类型, 例如text
        private String contentType;
        // 扩展参数
        private Map<String, String> extras;


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAlert() {
            return alert;
        }

        public void setAlert(String alert) {
            this.alert = alert;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public Map<String, String> getExtras() {
            return extras;
        }

        public void setExtras(Map<String, String> extras) {
            this.extras = extras;
        }

        @Override
        public String toString() {
            return "JBPushMessage{" +
                    "title='" + title + '\'' +
                    ", alert='" + alert + '\'' +
                    ", contentType='" + contentType + '\'' +
                    ", extras=" + extras +
                    '}';
        }
    }

    /**
     * Created by test on 2017/6/27.
     * <p>
     * 通知类消息，是会作为“通知”推送到客户端的
     */
    public static class JBPushNotification {
        // 标题
        private String title;
        // 内容 不能为空
        private String alert;
        // 通知提示音
        private String sound;
        // 应用角标
        private int badge;
        // 扩展参数
        private Map<String, String> extras;
        // ios静默推送选项
        private Boolean contentAvailable;
        // ios10支持的附件选项
        private Boolean mutableContent;


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAlert() {
            return alert;
        }

        public void setAlert(String alert) {
            this.alert = alert;
        }

        public String getSound() {
            return sound;
        }

        public void setSound(String sound) {
            this.sound = sound;
        }

        public int getBadge() {
            return badge;
        }

        public void setBadge(int badge) {
            this.badge = badge;
        }

        public Map<String, String> getExtras() {
            return extras;
        }

        public void setExtras(Map<String, String> extras) {
            this.extras = extras;
        }

        public Boolean getContentAvailable() {
            return contentAvailable;
        }

        public void setContentAvailable(Boolean contentAvailable) {
            this.contentAvailable = contentAvailable;
        }

        public Boolean getMutableContent() {
            return mutableContent;
        }

        public void setMutableContent(Boolean mutableContent) {
            this.mutableContent = mutableContent;
        }

        @Override
        public String toString() {
            return "JBPushNotification{" +
                    "title='" + title + '\'' +
                    ", alert='" + alert + '\'' +
                    ", sound='" + sound + '\'' +
                    ", badge=" + badge +
                    ", extras=" + extras +
                    ", contentAvailable=" + contentAvailable +
                    ", mutableContent=" + mutableContent +
                    '}';
        }
    }
}

