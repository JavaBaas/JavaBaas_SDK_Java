package com.javabaas.javasdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okio.Buffer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zangyilin on 2017/8/11.
 */
public class JBHttpParams {

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
            'B', 'C', 'D', 'E', 'F'};

    static final String QUERY_COMPONENT_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#&!$(),~";

    Map<String, JBParam> params;

    public JBHttpParams() {
        params = new HashMap<>();
    }

    public JBHttpParams(Map<String, ?> params) {
        this();
        if (params != null) {
            for (Map.Entry<String, ?> entry : params.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    public void put(String key, Object value) {
        String v;
        try {
            if (value instanceof Map) {
                ObjectMapper objectMapper = new ObjectMapper();
                v = objectMapper.writeValueAsString(value);
            } else {
                v = value.toString();
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("url错误");
        }
        params.put(canonicalize(key, QUERY_COMPONENT_ENCODE_SET, false, true), JBParam.getJBParam(v));
    }

    /**
     * 拼接BaseUrl和Params
     * @param url BaseUrl
     * @return wholeUrl
     */
    public String getWholeUrl(String url) {
        if (params.isEmpty()) {
            return url;
        } else {
            StringBuilder stringBuilder = new StringBuilder(url);
            stringBuilder.append("?");
            boolean first = true;

            for (Map.Entry<String, JBParam> entry : params.entrySet()) {
                if (!first) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue().encodedParam);
                first = false;
            }
            return stringBuilder.toString();
        }
    }

    /**
     * JBParam
     */
    private static class JBParam {
        String param;
        String encodedParam;

        public String getEncodedParam () {
            return encodedParam;
        }

        public String getParam() {
            return param;
        }

        public static JBParam getJBParam(String param, String encodedParam) {
            JBParam jbParam = new JBParam();
            jbParam.encodedParam = encodedParam;
            jbParam.param = param;
            return jbParam;
        }

        public static JBParam getJBParam(String param) {
            JBParam jbParam = new JBParam();
            jbParam.param = param;
            jbParam.encodedParam = canonicalize(param, QUERY_COMPONENT_ENCODE_SET, false, true);
            return jbParam;
        }
    }

    /************* 处理url转码和特殊字符问题开始 ***************/
    static String canonicalize(String input, int pos, int limit, String encodeSet,
                               boolean alreadyEncoded, boolean query) {
        int codePoint;
        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
            codePoint = input.codePointAt(i);
            if (codePoint < 0x20 || codePoint >= 0x7f || encodeSet.indexOf(codePoint) != -1
                    || (codePoint == '%' && !alreadyEncoded) || (query && codePoint == '+')) {
                Buffer out = new Buffer();
                out.writeUtf8(input, pos, i);
                canonicalize(out, input, i, limit, encodeSet, alreadyEncoded, query);
                return out.readUtf8();
            }
        }

        return input.substring(pos, limit);
    }

    static void canonicalize(Buffer out, String input, int pos, int limit, String encodeSet,
                             boolean alreadyEncoded, boolean query) {
        Buffer utf8Buffer = null; // Lazily allocated.
        int codePoint;
        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
            codePoint = input.codePointAt(i);
            if (alreadyEncoded
                    && (codePoint == '\t' || codePoint == '\n' || codePoint == '\f' || codePoint == '\r')) {
            } else if (query && codePoint == '+') {
                // HTML permits space to be encoded as '+'. We use '%20' to avoid special cases.
                out.writeUtf8(alreadyEncoded ? "%20" : "%2B");
            } else if (codePoint < 0x20 || codePoint >= 0x7f || encodeSet.indexOf(codePoint) != -1
                    || (codePoint == '%' && !alreadyEncoded)) {
                // Percent encode this character.
                if (utf8Buffer == null) {
                    utf8Buffer = new Buffer();
                }
                utf8Buffer.writeUtf8CodePoint(codePoint);
                while (!utf8Buffer.exhausted()) {
                    int b = utf8Buffer.readByte() & 0xff;
                    out.writeByte('%');
                    out.writeByte(HEX_DIGITS[(b >> 4) & 0xf]);
                    out.writeByte(HEX_DIGITS[b & 0xf]);
                }
            } else {
                // This character doesn't need encoding. Just copy it over.
                out.writeUtf8CodePoint(codePoint);
            }
        }
    }

    static String canonicalize(String input, String encodeSet, boolean alreadyEncoded, boolean query) {
        return canonicalize(input, 0, input.length(), encodeSet, alreadyEncoded, query);
    }

    /************* 处理url转码和特殊字符问题结束 ***************/

}
