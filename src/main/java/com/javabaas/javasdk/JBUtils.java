package com.javabaas.javasdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.internal.http2.Header;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by zangyilin on 2017/8/9.
 */
public class JBUtils {


    static Pattern pattern = Pattern.compile("^[a-zA-Z_][a-zA-Z_0-9]*$");
    public static final String CLASSNAME_TAG = "className";
    public static final String TYPE_TAG = "__type";


    public static void checkClassName(String className) {
        if (JBUtils.isEmpty(className))
            throw new IllegalArgumentException("classname为空");
        if (!pattern.matcher(className).matches())
            throw new IllegalArgumentException("classname不合法");
    }

    public static String stringFromBytes(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
        }
        return null;
    }


    public static String extractContentType(Header[] headers) {
        if (headers != null) {
            for (Header h : headers) {

                if (h.name.toString().equalsIgnoreCase("Content-Type")) {
                    return h.value.toString();
                }
            }
        }
        return null;
    }

    public static boolean checkAndSetValue(Class<?> clazz, Object parent, String property,
                                           Object value) {
        if (clazz == null) {
            return false;
        }
        try {
            Field fields[] = getAllFiels(clazz);
            for (Field f : fields) {
                if (f.getName().equals(property) && (f.getType().isInstance(value) || value == null)) {
                    f.set(parent, value);
                    return true;
                }
            }
            return false;
        } catch (Exception exception) {
            // TODO throw exception?
            // exception.printStackTrace();
        }
        return false;
    }

    private static Map<Class<?>, Field[]> fieldsMap = Collections
            .synchronizedMap(new WeakHashMap<Class<?>, Field[]>());

    public static Field[] getAllFiels(Class<?> clazz) {
        if (clazz == null || clazz == Object.class) {
            return new Field[0];
        }
        Field[] theResult = fieldsMap.get(clazz);
        if (theResult != null) {
            return theResult;
        }
        List<Field[]> fields = new ArrayList<>();
        int length = 0;
        while (clazz != null && clazz != Object.class) {
            Field[] declaredFields = clazz.getDeclaredFields();
            length += declaredFields != null ? declaredFields.length : 0;
            fields.add(declaredFields);
            clazz = clazz.getSuperclass();
        }
        theResult = new Field[length];
        int i = 0;
        for (Field[] someFields : fields) {
            if (someFields != null) {
                for (Field field : someFields) {
                    field.setAccessible(true);
                }
                System.arraycopy(someFields, 0, theResult, i, someFields.length);
                i += someFields.length;
            }
        }
        fieldsMap.put(clazz, theResult);
        return theResult;
    }

    public static boolean equals(String a, String b) {
        if (a == b)
            return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i))
                        return false;
                }
                return true;
            }
        }
        return false;
    }

    public static void copyPropertiesFromMapToObject(Object object, Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            checkAndSetValue(object.getClass(), object, entry.getKey(), entry.getValue());
        }
    }

    public static void copyPropertiesFromMapToJBObject(JBObject object, Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey().equals(JBObject.OBJECT_ID)) {
                object.setObjectId(entry.getValue().toString());
            } else if (entry.getKey().equals(JBObject.CREATED_AT)) {
                object.setCreatedAt(entry.getValue().toString());
            } else if (entry.getKey().equals(JBObject.UPDATED_AT)) {
                object.setUpdatedAt(entry.getValue().toString());
            } else {
                if (entry.getValue() instanceof Map) {
                    updatePropertyFromMap(object, entry.getKey(), (Map<String, Object>) entry.getValue());
                } else {
                    object.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public static void updatePropertyFromMap(JBObject parent, String key, Map<String, Object> map) {
        String objectId = (String) map.get(JBObject.OBJECT_ID);
        String type = (String) map.get(TYPE_TAG);
        if (JBUtils.isEmpty(type)) {
            parent.put(key, map);
            return;
        }
        if (type.equals("File")) {
            JBFile file = JBFile.getFileFromMap(map);
            parent.put(key, file);
        } else if (type.equals("Pointer") || (!JBUtils.isEmpty(objectId) && !JBUtils.isEmpty(type))) {
            JBObject object = parseObjectFromMap(map);
            parent.put(key, object);
        } else {
            parent.put(key, map);
        }
    }


    public static JBObject parseObjectFromMap(Map<String, Object> map) {
        JBObject object = newJBObjectByClassName((String) map.get(CLASSNAME_TAG));
        copyPropertiesFromMapToJBObject(object, map);
        return object;
    }

    public static JBObject newJBObjectByClassName(String className) {
        if (className.equals(JBUser.userClassName())) {
            return new JBUser();
        } else {
            return new JBObject(className);
        }
    }

    private static final ThreadLocal<SimpleDateFormat> THREAD_LOCAL_DATE_FORMAT =
            new ThreadLocal<SimpleDateFormat>();
    private static final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static Date dateFromString(String content) {
        if (isEmpty(content))
            return null;
        if (isDigitString(content)) {
            return new Date(Long.parseLong(content));
        }
        Date date = null;
        SimpleDateFormat format = THREAD_LOCAL_DATE_FORMAT.get();
        // reuse date format.
        if (format == null) {
            format = new SimpleDateFormat(dateFormat);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            THREAD_LOCAL_DATE_FORMAT.set(format);
        }
        try {
            date = format.parse(content);
        } catch (Exception exception) {

        }
        return date;
    }

    public static boolean isDigitString(String s) {
        if (s == null) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /****************JSON BEGIN****************/
    public static <T> T readValue(String content, Class<T> valueType) throws JBException{
        if (content == null) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(content, valueType);
        } catch (IOException e) {
            throw new JBException(JBCode.INTERNAL_JSON_ERROR);
        }
    }

    public static String writeValueAsString(Object value) throws JBException{
        if (value != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                return objectMapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                throw new JBException(JBCode.INTERNAL_JSON_ERROR);
            }
        } else {
            return "";
        }
    }
    /****************JSON END****************/

    public static List<Double> listFromGeoPoint(JBGeoPoint point) {
        List<Double> list = new ArrayList<>();
        list.add(point.getLongitude());
        list.add(point.getLatitude());
        return list;
    }

    public static Map<String, Object> mapFromGeoPoint(JBGeoPoint point) {
        Map<String, Object> result = new LinkedHashMap<>(16);
        result.put(JBGeoPoint.LONGTITUDE_KEY, point.getLongitude());
        result.put(JBGeoPoint.LATITUDE_KEY, point.getLatitude());
        result.put("__type", "GeoPoint");
        return result;
    }

    public static Map<String, Object> createMap(String cmp, Object value) {
        Map<String, Object> dict = new HashMap<String, Object>();
        dict.put(cmp, value);
        return dict;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.trim().equals("");
    }


    public static String md5DigestAsHex(String string) {
        byte[] bytes = string.getBytes();
        return digestAsHexString("MD5", bytes);
    }

    private static String digestAsHexString(String algorithm, byte[] bytes) {
        char[] hexDigest = digestAsHexChars(algorithm, bytes);
        return new String(hexDigest);
    }

    private static char[] digestAsHexChars(String algorithm, byte[] bytes) {
        byte[] digest = digest(algorithm, bytes);
        return encodeHex(digest);
    }

    private static byte[] digest(String algorithm, byte[] bytes) {
        return getDigest(algorithm).digest(bytes);
    }

    private static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException var2) {
            throw new IllegalStateException("Could not find MessageDigest with algorithm \"" + algorithm + "\"", var2);
        }
    }

    private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static char[] encodeHex(byte[] bytes) {
        char[] chars = new char[32];

        for(int i = 0; i < chars.length; i += 2) {
            byte b = bytes[i / 2];
            chars[i] = HEX_CHARS[b >>> 4 & 15];
            chars[i + 1] = HEX_CHARS[b & 15];
        }

        return chars;
    }

}
