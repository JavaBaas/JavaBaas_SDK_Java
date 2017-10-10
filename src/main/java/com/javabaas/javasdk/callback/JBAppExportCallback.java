package com.javabaas.javasdk.callback;

import com.javabaas.javasdk.JBApp;
import com.javabaas.javasdk.JBException;

/**
 * Created by zangyilin on 2017/10/10.
 */
public abstract class JBAppExportCallback {
    public abstract void done(boolean success, JBApp.JBAppExport appExport, JBException e);
}
