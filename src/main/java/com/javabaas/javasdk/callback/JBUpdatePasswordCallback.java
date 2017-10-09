package com.javabaas.javasdk.callback;


import com.javabaas.javasdk.JBException;

/**
 * Created by zangyilin on 2017/9/19.
 */
public abstract class JBUpdatePasswordCallback {
    public abstract void done(boolean success, String sessionToken, JBException e);
}
