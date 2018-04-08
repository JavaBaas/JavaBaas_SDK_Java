package com.javabaas.javasdk.callback;

import com.javabaas.javasdk.JBException;
import com.javabaas.javasdk.JBFile;

/**
 * Created by zangyilin on 2018/4/8.
 */
public abstract class JBFileProcessCallback {
    public abstract void done(boolean success, JBFile file, JBException e);
}
