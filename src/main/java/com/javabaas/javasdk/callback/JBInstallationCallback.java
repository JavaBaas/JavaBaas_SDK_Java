package com.javabaas.javasdk.callback;

import com.javabaas.javasdk.JBException;

/**
 * Created by zangyilin on 2017/11/15.
 */
public abstract class JBInstallationCallback {
    public abstract void done(boolean success, String installationId, JBException e);
}
