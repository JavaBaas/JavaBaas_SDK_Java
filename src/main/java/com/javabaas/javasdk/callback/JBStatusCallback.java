package com.javabaas.javasdk.callback;

import com.javabaas.javasdk.JBException;

import java.util.Map;

/**
 * Created by zangyilin on 2017/10/30.
 */
public abstract class JBStatusCallback {
    public abstract void done(boolean success, Map<String, Object> status, JBException e);
}
