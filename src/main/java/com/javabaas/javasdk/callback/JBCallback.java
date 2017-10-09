package com.javabaas.javasdk.callback;


import com.javabaas.javasdk.JBException;

/**
 * Created by zangyilin on 2017/8/16.
 */
public abstract class JBCallback<T> {
    protected abstract void callback(boolean success, T t, JBException e);


}
