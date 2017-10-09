package com.javabaas.javasdk;

/**
 * Created by Staryet on 15/6/18.
 */
public class JBException extends RuntimeException {

    private int code;
    private String message;

    public static void e(JBCode simpleCode) {
        throw new JBException(simpleCode);
    }

    public JBException(JBCode simpleCode) {
        code = simpleCode.getCode();
        message = simpleCode.getMessage();
    }

    public JBException(int code) {
        this.code = code;
        this.message = "";
    }

    public JBException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
