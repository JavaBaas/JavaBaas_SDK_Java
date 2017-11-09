package com.javabaas.javasdk;

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

    private String url;
    private String name;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public JBFile(){
        super(fileClassName());
    }

    public JBFile(String name, String url) {
        super(fileClassName());
        this.name = name;
        this.url = url;
    }

//    public JBObject getFileObject() {
//        if (fileObject == null && !StringUtils.isEmpty(objectId)) {
//            fileObject = JBObject.createWithOutData("_File", objectId);
//        }
//        return fileObject;
//    }

    public static String fileClassName() {
        return "_File";
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
        if (!JBUtils.isEmpty(url)) {
            stringBuffer.append(", \"url\":\"" + url + "\"");
        }
        if (!JBUtils.isEmpty(name)) {
            stringBuffer.append(", \"name\":\"" + name + "\"");
        }
        stringBuffer.append("}");
        return stringBuffer.toString();
    }
}
