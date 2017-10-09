package com.javabaas.javasdk.callback;


import com.javabaas.javasdk.JBException;
import com.javabaas.javasdk.JBObject;

/**
 * Created by zangyilin on 2017/9/12.
 */
public abstract class JBGetCallback<T extends JBObject>{
    public abstract void done(boolean success, T object, JBException e);
}
