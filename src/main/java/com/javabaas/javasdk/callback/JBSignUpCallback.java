package com.javabaas.javasdk.callback;


import com.javabaas.javasdk.JBException;

/**
 * Created by zangyilin on 2017/9/19.
 */
public abstract class JBSignUpCallback {
    public abstract void done(boolean success, JBException e);
}
