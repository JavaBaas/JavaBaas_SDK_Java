package com.javabaas.javasdk.callback;


import com.javabaas.javasdk.JBException;

/**
 * Created by zangyilin on 2017/9/13.
 */
public abstract class JBDeleteCallback {
    public abstract void done(boolean success, JBException e);
}
