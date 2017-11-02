package com.javabaas.javasdk;

import com.javabaas.javasdk.callback.JBObjectCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.internal.http2.Header;

import java.io.IOException;

/**
 * Created by zangyilin on 2017/8/15.
 */
public abstract class JBHttpResponseHandler implements Callback {
    protected JBObjectCallback callback;

    public JBHttpResponseHandler() {}

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
        this.onFailure(new JBException(JBCode.OTHER_HTTP_ERROR));
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        String content = JBUtils.stringFromBytes(response.body().bytes());
        JBResult result = null;
        try {
            if (response.code() == 200) {
                result = JBUtils.readValue(content, JBResult.class);
                this.onSuccess(result);
            } else if (response.code() == 400 || response.code() == 500) {
                result = JBUtils.readValue(content, JBResult.class);
                this.onFailure(new JBException(result.getCode(), result.getMessage()));
            } else {
                this.onFailure(new JBException(JBCode.OTHER_HTTP_ERROR));
            }
        } catch (JBException e) {
            this.onFailure(e);
        }
    }

    public abstract void onSuccess(JBResult result);

    public abstract void onFailure(JBException error);

    static Header[] getHeaders(Headers headers) {
        if (headers != null && headers.size() > 0) {
            Header[] httpHeaders = new Header[headers.size()];
            for (int i = 0; i < headers.names().size(); i ++) {
                final String key = headers.name(i);
                final String value = headers.get(key);
                httpHeaders[i] = new Header(key, value);
            }
            return httpHeaders;
        } else {
            return null;
        }
    }

}
