package com.javabaas.javasdk.callback;

import com.javabaas.javasdk.JBException;

import java.util.Map;

/**
 * Created by zangyilin on 2017/11/10.
 */
public abstract class JBFileTokenCallback {
    public abstract void done(boolean success, Map<String, Object> data, JBException e);
}
