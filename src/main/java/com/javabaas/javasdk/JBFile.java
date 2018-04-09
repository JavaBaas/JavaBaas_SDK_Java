package com.javabaas.javasdk;

import com.javabaas.javasdk.callback.JBFileProcessCallback;
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

    /**
     * 新建file后是否需要fetch信息
     * @param need needFetch
     */
    public void needFetchFile(boolean need) {
        this.needFetch = need;
    }

    /**
     * 获取文件上传token 同步
     *
     * @param fileName      文件名称
     * @param platform      上传目的平台 例如"qiniu"
     * @param policy        上传策略
     * @return              token信息
     * @throws JBException  异常信息
     */
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

    /**
     * 获取文件上传token 异步
     *
     * @param fileName      文件名称
     * @param platform      上传目的平台 例如"qiniu"
     * @param policy        上传策略
     * @param callback      token回调
     */
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

    /**
     * 保存文件 同步
     * 该方法只是保存JBFile对象,如果对象中的url是对应其他网络文件,
     * 希望拷贝到目的文件储存平台可以使用needFetchFile,设置为tre
     * 服务端会自动将网络文件下载后保存到目的文件储存
     *
     * 需要注意的是,如果needFetchFile设置为true,对应的网络文件大小不能太大.太大可能会失败.
     *
     * @param platform      保存目的平台 例如"qiniu"
     * @param policy        保存策略
     * @return              保存后的JBFile信息
     * @throws JBException  异常信息
     */
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

    /**
     * 保存文件 异步<br/>
     * 该方法只是保存JBFile对象,如果对象中的url是对应其他网络文件,<br/>
     * 希望拷贝到目的文件储存平台可以使用needFetchFile,设置为tre<br/>
     * 服务端会自动将网络文件下载后保存到目的文件储存<br/>
     *
     * 需要注意的是,如果needFetchFile设置为true,对应的网络文件大小不能太大.太大可能会失败.
     *
     * @param platform      保存目的平台 例如"qiniu"
     * @param policy        保存策略
     * @param callback      文件保存回调
     */
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

    public static JBFile process(String fileId, String platform, Map<String, Object> policy) throws JBException {
        final JBFile[] result = {null};
        processFileWithJavabaas(fileId, platform, policy, true, new JBFileProcessCallback() {
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

    public static void processInBackground(String fileId, String platform, Map<String, Object> policy, JBFileProcessCallback callback) {
        processFileWithJavabaas(fileId, platform, policy, false, callback);
    }

    private static void processFileWithJavabaas(final String fileId, final String platform, final Map<String, Object> policy, final boolean sync, final JBFileProcessCallback callback) {
        String path = JBHttpClient.getFilePath("master/process");
        JBHttpParams params = new JBHttpParams();
        if (!JBUtils.isEmpty(fileId)) {
            params.put("fileId", fileId);
        }
        if (!JBUtils.isEmpty(platform)) {
            params.put("platform", platform);
        }
        if (policy != null) {
            params.put("policy", policy);
        }
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, params, null, sync, new JBObjectCallback() {
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
