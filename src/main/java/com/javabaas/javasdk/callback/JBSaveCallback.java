package com.javabaas.javasdk.callback;


import com.javabaas.javasdk.JBException;

/**
 * Created by zangyilin on 2017/8/9.
 */
public abstract class JBSaveCallback {
    public abstract void done(boolean success, JBException e);

}
