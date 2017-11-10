package com.javabaas.javasdk;

import com.javabaas.javasdk.callback.JBFileSaveCallback;
import com.javabaas.javasdk.callback.JBFileTokenCallback;
import com.javabaas.javasdk.callback.JBObjectCallback;

import java.util.Map;

/**
 * Created by zangyilin on 2017/9/11.
 */
public final class JBFile extends JBObject{

    private static final String URL = "url";
    private static final String SIZE = "size";
    private static final String NAME = "name";
    private static final String KEY = "key";
    private static final String MIMETYPE = "mimeType";

    private volatile boolean needFetch = false;

    public String getUrl() {
        return (String) get("url");
    }

    public void setUrl(String url) {
        put("url", url);
    }

    public String getName() {
        return (String) get("name");
    }

    public void setName(String name) {
        put("name", name);
    }


    public JBFile(){
        super(fileClassName());
    }

    public JBFile(String name, String url) {
        super(fileClassName());
        setName(name);
        setUrl(url);
    }

    public static String fileClassName() {
        return "_File";
    }

    public void needFetchFile(boolean need) {
        this.needFetch = need;
    }

    public static Map<String, Object> getToken(String fileName, String platform, String policy) throws JBException {
        final Map<String, Object>[] result = new Map[]{null};
        getTokenFromJavabaas(fileName, platform, policy, true, new JBFileTokenCallback() {
            @Override
            public void done(boolean success, Map<String, Object> data, JBException e) {
                if (success) {
                    result[0] =  data;
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

    public static void getTokenInBackground(String fileName, String platform, String policy, JBFileTokenCallback callback) {
        getTokenFromJavabaas(fileName, platform, policy, false, callback);
    }

    private static void getTokenFromJavabaas(final String fileName, final String platform, final String policy, final boolean sync, final JBFileTokenCallback callback) {
        String path = JBHttpClient.getFilePath("getToken");
        JBHttpParams params = new JBHttpParams();
        if (!JBUtils.isEmpty(fileName)) {
            params.put("fileName", fileName);
        }
        if (!JBUtils.isEmpty(platform)) {
            params.put("platform", platform);
        }
        if (!JBUtils.isEmpty(policy)) {
            params.put("policy", policy);
        }
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, params, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback != null) {
                    callback.done(true, result.getData(), null);
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

    public JBFile saveFile(String platform, String policy) throws JBException {
        final JBFile[] result = {null};
        saveFileToJavabaas(platform, policy, true, new JBFileSaveCallback() {
            @Override
            public void done(boolean success, JBFile file, JBException e) {
                if (success) {
                    result[0] = file;
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

    public void saveFileInBackground(String platform, String policy, JBFileSaveCallback callback) {
        saveFileToJavabaas(platform, policy, false, callback);
    }

    private void saveFileToJavabaas(final String platform, final String policy, final boolean sync, final JBFileSaveCallback callback) {
        String path = JBHttpClient.getFilePath(null);
        JBHttpParams params = new JBHttpParams();
        if (this.needFetch) {
            params.put("fetch", true);
        }
        if (!JBUtils.isEmpty(platform)) {
            params.put("platform", platform);
        }
        if (!JBUtils.isEmpty(policy)) {
            params.put("policy", policy);
        }
        Map<String, Object> body = getObjectForSaveBody();
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, params, body, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback != null) {
                    Map<String, Object> data = result.getData();
                    callback.done(true, fileFromMap((Map<String, Object>) data.get("file")), null);
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

    public static JBFile fileFromMap(Map<String, Object> map) {
        JBFile file = new JBFile();
        String objectId = (String) map.get(OBJECT_ID);
        String url = (String) map.get(URL);
        String name = (String) map.get(NAME);
        if (!JBUtils.isEmpty(objectId)) {
            file.setObjectId(objectId);
        }
        if (!JBUtils.isEmpty(url)) {
            file.setUrl(url);
        }
        if (!JBUtils.isEmpty(name)) {
            file.setName(name);
        }
        return file;
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("{");
        if (!JBUtils.isEmpty(className)) {
            stringBuffer.append("\"className\":\"" + className + "\"");
        }
        if (!JBUtils.isEmpty(objectId)) {
            stringBuffer.append(", \"objectId\":\"" + objectId + "\"");
        }
        if (get("url") != null) {
            stringBuffer.append(", \"url\":\"" + get("url") + "\"");
        }
        if (get("name") != null) {
            stringBuffer.append(", \"name\":\"" + get("name") + "\"");
        }
        stringBuffer.append("}");
        return stringBuffer.toString();
    }
}
