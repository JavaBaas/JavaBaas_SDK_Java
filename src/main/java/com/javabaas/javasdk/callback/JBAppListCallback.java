package com.javabaas.javasdk.callback;

import com.javabaas.javasdk.JBApp;
import com.javabaas.javasdk.JBException;

import java.util.List;

/**
 * Created by zangyilin on 2017/9/21.
 */
public abstract class JBAppListCallback {
    public abstract void done(boolean success, List<JBApp> list, JBException e);
}
