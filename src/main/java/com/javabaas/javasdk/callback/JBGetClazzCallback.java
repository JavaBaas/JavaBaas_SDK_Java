package com.javabaas.javasdk.callback;

import com.javabaas.javasdk.JBClazz;
import com.javabaas.javasdk.JBException;

/**
 * Created by zangyilin on 2017/9/21.
 */
public abstract class JBGetClazzCallback {
    public abstract void done(boolean success, JBClazz clazz, JBException e);
}
