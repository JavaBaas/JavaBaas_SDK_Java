package com.javabaas.javasdk;

/**
 * Created by Staryet on 15/6/18.
 */
public class JBException extends Exception {

    private int code;
    private String message;

    public JBException(JBCode simpleCode) {
        super(simpleCode.getMessage());
        code = simpleCode.getCode();
        message = simpleCode.getMessage();
    }

    public JBException(int code) {
        super();
        this.code = code;
        this.message = "";
    }

    public JBException(int code, String message) {
        super(message);
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
