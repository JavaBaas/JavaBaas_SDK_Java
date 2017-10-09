package com.javabaas.javasdk.callback;

import com.javabaas.javasdk.JBApp;
import com.javabaas.javasdk.JBException;

/**
 * Created by zangyilin on 2017/9/21.
 */
public abstract class JBGetAppCallback {
    public abstract void done(boolean success, JBApp app, JBException e);
}
