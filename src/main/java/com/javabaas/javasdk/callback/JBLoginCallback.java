package com.javabaas.javasdk.callback;


import com.javabaas.javasdk.JBException;
import com.javabaas.javasdk.JBUser;

/**
 * Created by zangyilin on 2017/9/18.
 */
public abstract class JBLoginCallback {
    public abstract void done(boolean success, JBUser user, JBException e);
}
