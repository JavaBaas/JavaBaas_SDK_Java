package com.javabaas.javasdk.callback;

import com.javabaas.javasdk.JBClazz;
import com.javabaas.javasdk.JBException;

import java.util.List;

/**
 * Created by zangyilin on 2017/9/21.
 */
public abstract class JBClazzListCallback {
    public abstract void done(boolean success, List<JBClazz> list, JBException e);
}
