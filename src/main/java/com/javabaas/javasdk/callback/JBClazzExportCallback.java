package com.javabaas.javasdk.callback;

import com.javabaas.javasdk.JBClazz;
import com.javabaas.javasdk.JBException;

/**
 * Created by zangyilin on 2017/10/10.
 */
public abstract class JBClazzExportCallback {
    public abstract void done(boolean success, JBClazz.JBClazzExport clazzExport, JBException e);
}
