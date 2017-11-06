package com.javabaas.javasdk.callback;

import com.javabaas.javasdk.JBApp;
import com.javabaas.javasdk.JBException;

import java.util.List;

/**
 * Created by zangyilin on 2017/11/6.
 */
public abstract class JBAppConfigListCallback {
    public abstract void done(boolean success, List<JBApp.JBAppConfig> list, JBException e);
}
