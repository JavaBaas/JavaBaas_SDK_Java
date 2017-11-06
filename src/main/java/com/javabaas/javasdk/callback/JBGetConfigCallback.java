package com.javabaas.javasdk.callback;

import com.javabaas.javasdk.JBException;

/**
 * Created by zangyilin on 2017/11/3.
 */
public abstract class JBGetConfigCallback {
    public abstract void done(boolean success, String value, JBException e);
}
