package com.javabaas.javasdk.callback;

import com.javabaas.javasdk.JBException;

/**
 * Created by zangyilin on 2017/11/10.
 */
public abstract class JBBooleanCallback {
    public abstract void done(boolean success, JBException e);
}
