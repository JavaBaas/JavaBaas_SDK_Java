package com.javabaas.javasdk.callback;

import com.javabaas.javasdk.JBException;
import com.javabaas.javasdk.JBFile;

/**
 * Created by zangyilin on 2017/11/10.
 */
public abstract class JBFileSaveCallback {
    public abstract void done(boolean success, JBFile file, JBException e);
}
