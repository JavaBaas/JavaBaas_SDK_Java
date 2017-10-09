package com.javabaas.javasdk.callback;


import com.javabaas.javasdk.JBException;

/**
 * Created by zangyilin on 2017/9/12.
 */
public abstract class JBCountCallback{

    public abstract void done(boolean success, int count, JBException e);

}
